package ru.emitrohin.privateclubbackend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.emitrohin.privateclubbackend.dto.request.TelegramInitDataRequest;
import ru.emitrohin.privateclubbackend.dto.request.TelegramUserRequest;
import ru.emitrohin.privateclubbackend.dto.response.JwtAuthenticationResponse;
import ru.emitrohin.privateclubbackend.model.User;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
@RequiredArgsConstructor
public class TelegramAuthenticationService {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${telegram.botToken}")
    private String botToken;

    @Value("${telegram.authExpirationTime}")
    private long expirationTime;

    private final UserService userService;
    private final JwtService jwtService;

    // telegram id is used as unique name
    public JwtAuthenticationResponse authenticateTelegram(TelegramInitDataRequest request) throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {

        Map<String, String> parsedData = parseData(request.initData());
        boolean initDataIsValid = isInitDataValid(parsedData);

        //TODO правильно ли так делать?
        if (!initDataIsValid) { throw new RuntimeException("Invalid telegram request"); }

        //извлечь данные из parsedData и смапить на TelegramUserRequest
        String userJson = URLDecoder.decode(parsedData.get("user"), StandardCharsets.UTF_8);
        TelegramUserRequest telegramUserRequest = objectMapper.readValue(userJson, TelegramUserRequest.class);
        long telegramId = telegramUserRequest.telegramId();

        // сохранить в БД, если нет
        Optional<User> user = userService.findByTelegramId(telegramId);
        if (user.isEmpty()) {
            userService.save(telegramUserRequest);
        }

        String jwt = jwtService.generateToken(telegramId);
        return new JwtAuthenticationResponse(jwt);
    }

    /*
        Парсим пары, и раскладываем в хэш таблицу
        query_id=<query_id>auth_date=<unix_time>&user={“id":123456789,“first_name":“John",“last_name":""}&hash=123abc
    */
    private Map<String, String> parseData(String data)  {
        String[] pairs = URLDecoder.decode(data, StandardCharsets.UTF_8).split("&");
        Map<String, String> parsedData = new HashMap<>();
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            parsedData.put(pair.substring(0, idx), pair.substring(idx + 1));
        }
        return parsedData;
    }

    public boolean isInitDataValid(Map<String, String> values) throws NoSuchAlgorithmException, InvalidKeyException {
        String calculatedHash = validateIntegrity(new HashMap<>(values));
        long currentTime = System.currentTimeMillis() / 1000;
        long authTime = Long.parseLong(values.get("auth_date"));
        long timeDifference = currentTime - authTime;

        String receivedHash = values.get("hash");

        //сравниваем рассчитанный хэш и разницу между временем запроса и временем экспирации
        return calculatedHash.equalsIgnoreCase(receivedHash) && (timeDifference <= expirationTime);
    }

    /*
        1. Исключаем hash формируем строку из пар, сортируя по алфавиту, соединяя "\n"
        2. Считаем hash строки
    */
    private String validateIntegrity(Map<String, String> data) throws NoSuchAlgorithmException, InvalidKeyException {
        data.remove("hash");

        //формируем строку из пар, сортируя ключи по алфавиту, соединяя "\n"
        String[] keyPairs = data.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .toArray(String[]::new);
        String dataCheckString = String.join("\n", keyPairs);

        // Initial HMAC with "WebAppData"
        Mac initialMac = Mac.getInstance("HmacSHA256");
        SecretKeySpec initialKey = new SecretKeySpec("WebAppData".getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        initialMac.init(initialKey);
        byte[] initialHash = initialMac.doFinal(botToken.getBytes(StandardCharsets.UTF_8));

        // HMAC using the initial hash as the key
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(initialHash, "HmacSHA256");
        mac.init(secretKey);
        byte[] hashBytes = mac.doFinal(dataCheckString.getBytes(StandardCharsets.UTF_8));

        //делаем hex строку хэша
        StringBuilder hexString = new StringBuilder();
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
