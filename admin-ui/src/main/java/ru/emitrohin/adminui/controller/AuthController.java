package ru.emitrohin.adminui.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.emitrohin.adminui.dto.request.LoginPasswordRequest;
import ru.emitrohin.adminui.dto.response.JwtAuthenticationResponse;
import ru.emitrohin.adminui.services.LoginPasswordAuthenticationService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginPasswordAuthenticationService loginPasswordAuthenticationService;

    @PostMapping()
    public JwtAuthenticationResponse authenticateAdmin(@RequestBody @Valid LoginPasswordRequest loginRequest) {
        return loginPasswordAuthenticationService.authenticateAdmin(loginRequest);
    }
}