package ru.emitrohin.privateclubbackend.dto.request.telegram;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;


//TODO есть сомнения в его нужности
@JsonIgnoreProperties(ignoreUnknown = true)
public record TelegramUserRequest(

        @NotNull(message = "Telegram ID cannot be null")
        @Positive(message = "Telegram ID must be positive")
        @JsonProperty("id")
        Long telegramId,

        @NotNull
        @JsonProperty("first_name")
        String firstName,

        @JsonProperty("username")
        String lastName,

        @JsonProperty("last_name")
        String username,

        @JsonProperty("language_code")
        String language_code,

        @Pattern(regexp = "^(|http(s)?://.+$)")
        @JsonProperty("photo_url")
        String photoUrl) { }