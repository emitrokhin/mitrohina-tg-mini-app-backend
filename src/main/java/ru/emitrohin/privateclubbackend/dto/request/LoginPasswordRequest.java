package ru.emitrohin.privateclubbackend.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginPasswordRequest(
        @NotBlank(message = "Username can't be blank")
        String username,

        @NotBlank(message = "Password can't be blank")
        String password
) { }

