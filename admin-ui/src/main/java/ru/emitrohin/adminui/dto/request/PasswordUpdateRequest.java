package ru.emitrohin.adminui.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PasswordUpdateRequest(

        @NotBlank(message = "Password can't be blank")
        String password
) { }

