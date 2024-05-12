package ru.emitrohin.privateclubbackend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.URL;

import java.util.UUID;

public record UserResponse(

        UUID id,

        @NotNull(message = "Telegram ID cannot be null")
        @Positive(message = "Telegram ID must be positive")
        Long telegramId,

        String firstName,
        String lastName,
        String username,

        @URL(message = "Photo URL must be a valid URL")
        String photoUrl) { }