package ru.emitrohin.adminui.dto.response;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record AdminUserResponse (
        @NotNull
        UUID id,

        @NotBlank
        String username
){ }