package ru.emitrohin.privateclubbackend.config.properties;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "telegram")
@Validated
public record TelegramProperties(
        @NotNull String botToken,
        @Positive long authExpirationTime
) {
}
