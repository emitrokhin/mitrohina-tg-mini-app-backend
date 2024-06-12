package ru.emitrohin.adminui.dto.response;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.URL;

import java.util.UUID;

public record UserResponse(

        @NotNull
        UUID id,

        @NotNull(message = "Telegram ID cannot be null")
        @Positive(message = "Telegram ID must be positive")
        Long telegramId,

        @NotNull(message = "First name cannot be null")
        String firstName,

        //TODO становится null. прийти к одному виду
        String lastName,

        //TODO становится пустым. прийти к одному виду
        String username,

        @URL(message = "Photo URL must be a valid URL")
        String photoUrl) { }