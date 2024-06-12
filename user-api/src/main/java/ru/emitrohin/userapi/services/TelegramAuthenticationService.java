package ru.emitrohin.userapi.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import ru.emitrohin.userapi.config.properties.TelegramProperties;
import ru.emitrohin.userapi.dto.request.telegram.TelegramInitDataRequest;
import ru.emitrohin.userapi.dto.request.telegram.TelegramUserRequest;
import ru.emitrohin.userapi.dto.response.JwtAuthenticationResponse;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 Данные, полученные через мини-приложение, представляют собой URL-кодированную строку следующего формата:

 query_id=<query_id>auth_date=<unix_time>&user={"id":123456789,"first_name":"John","last_name":""}&hash=123abc

 Параметр hash является шестнадцатеричным представлением сигнатуры hmac-sha-256 для строки проверки данных,
 которая генерируется следующим образом:

 Отсортируйте все пары (кроме hash) ключ/значение полученных данных в алфавитном порядке по имени ключа по возрастанию.
 Например, строка проверки данных для приведенного выше примера будет выглядеть так:

 auth_date=1614811840&query_id=123456789&user={"first_name":"John","id":123456789,"last_name":""}

 Сперва нужно рассчитать сигнатуру hmac-sha-256 для строки проверки данных, используя токен бота в качестве секретного ключа.
 Далее сравнить полученную сигнатуру с параметром hash. Если сигнатуры совпадают, данные являются валидными.

 Так же проверятся параметр auth_date (формат unix_time). Если разница между текущим временем и временем auth_date
 превышает expirationTime, то данные не валидны. Эта проверка необходима для предотвращения replay атак.
 */
@Service
@EnableConfigurationProperties(TelegramProperties.class)
@RequiredArgsConstructor
public class TelegramAuthenticationService {

    private final ObjectMapper objectMapper;

    private final TelegramProperties telegramProperties;

    private final UserService userService;

    private final JwtService jwtService;

    private final Clock clock;

    // telegram id is used as unique name
    public JwtAuthenticationResponse authenticateTelegram(TelegramInitDataRequest request) throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {

        var queryParams = parseInitData(request.initData());

        if (!isInitDataValid(queryParams)) {
            throw new RuntimeException("Telegram initData is not valid");
        }

        if (isInitDataExpired(queryParams.get("auth_date"))) {
            throw new RuntimeException("Telegram initData is expired");
        }

        //извлечь данные из queryParams и смапить на TelegramUserRequest
        var telegramUserRequest = objectMapper.readValue(queryParams.get("user"), TelegramUserRequest.class);
        var telegramId = telegramUserRequest.telegramId();

        // сохранить в БД, если нет
        var user = userService.findByTelegramId(telegramId);
        if (user.isEmpty()) {
            userService.save(telegramUserRequest);
        }

        var jwt = jwtService.generateTokenForTelegramUserId(telegramId);
        return new JwtAuthenticationResponse(jwt);
    }

    /*
        Парсим пары, и раскладываем в хэш таблицу
        query_id=<query_id>&auth_date=<unix_time>&user={“id":123456789,“first_name":“John",“last_name":""}&hash=123abc
    */
    private Map<String, String> parseInitData(String queryString)  {
        var queryParams = new HashMap<String, String>();
        var decodedInitData = URLDecoder.decode(queryString, StandardCharsets.UTF_8);
        UriComponentsBuilder.fromUriString("?" + decodedInitData).build()
                .getQueryParams()
                .forEach((k, v) -> queryParams.put(k, v.getFirst()));
        return queryParams;
    }

    private boolean isInitDataValid(Map<String, String> values) throws NoSuchAlgorithmException, InvalidKeyException {
        var calculatedHash = validateIntegrity(new HashMap<>(values));
        var receivedHash = values.get("hash");
        return calculatedHash.equalsIgnoreCase(receivedHash);
    }

    private boolean isInitDataExpired(String authDate) {
        var authTime = Instant.ofEpochSecond(Long.parseLong(authDate));
        return clock.instant().compareTo(authTime) >= telegramProperties.authExpirationTime();

    }

    /*
        1. Исключаем hash формируем строку из пар, сортируя по алфавиту, соединяя "\n"
        2. Считаем hash строки
    */
    private String validateIntegrity(Map<String, String> data) throws NoSuchAlgorithmException, InvalidKeyException {
        data.remove("hash");

        //формируем строку из пар, сортируя ключи по алфавиту, соединяя "\n"
        var keyPairs = data.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .toArray(String[]::new);
        var dataCheckString = String.join("\n", keyPairs);

        // Initial HMAC with "WebAppData"
        var initialMac = Mac.getInstance("HmacSHA256");
        var initialKey = new SecretKeySpec("WebAppData".getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        initialMac.init(initialKey);
        byte[] initialHash = initialMac.doFinal(telegramProperties.botToken().getBytes(StandardCharsets.UTF_8));

        // HMAC using the initial hash as the key
        var mac = Mac.getInstance("HmacSHA256");
        var secretKey = new SecretKeySpec(initialHash, "HmacSHA256");
        mac.init(secretKey);
        byte[] hashBytes = mac.doFinal(dataCheckString.getBytes(StandardCharsets.UTF_8));

        //делаем hex строку хэша
        var hexString = new StringBuilder();
        for (byte hashByte : hashBytes) {
            String hex = Integer.toHexString(0xff & hashByte);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
