package ru.emitrohin.privateclubbackend.dto.response;

import jakarta.validation.constraints.NotEmpty;

public record JwtAuthenticationResponse(
        @NotEmpty(message = "Token cant be empty")
        String token) { }