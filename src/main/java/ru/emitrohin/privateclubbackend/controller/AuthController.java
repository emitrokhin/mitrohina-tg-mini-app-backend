package ru.emitrohin.privateclubbackend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.emitrohin.privateclubbackend.dto.request.LoginPasswordRequest;
import ru.emitrohin.privateclubbackend.dto.request.telegram.TelegramInitDataRequest;
import ru.emitrohin.privateclubbackend.dto.response.JwtAuthenticationResponse;
import ru.emitrohin.privateclubbackend.service.LoginPasswordAuthenticationService;
import ru.emitrohin.privateclubbackend.service.TelegramAuthenticationService;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final TelegramAuthenticationService telegramAuthenticationService;

    private final LoginPasswordAuthenticationService loginPasswordAuthenticationService;

    @PostMapping("/telegram")
    public JwtAuthenticationResponse authenticateTelegram(@RequestBody @Valid TelegramInitDataRequest initData) throws UnsupportedEncodingException,
            NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        return telegramAuthenticationService.authenticateTelegram(initData);
    }

    @PostMapping("/admin")
    public JwtAuthenticationResponse authenticateAdmin(@RequestBody @Valid LoginPasswordRequest loginRequest) {
        return loginPasswordAuthenticationService.authenticateAdmin(loginRequest);
    }
}