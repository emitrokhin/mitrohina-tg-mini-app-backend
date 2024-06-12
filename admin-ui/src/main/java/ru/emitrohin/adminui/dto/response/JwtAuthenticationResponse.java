package ru.emitrohin.adminui.dto.response;

import jakarta.validation.constraints.NotEmpty;

public record JwtAuthenticationResponse(
        @NotEmpty(message = "Token cant be empty")
        String token) { }