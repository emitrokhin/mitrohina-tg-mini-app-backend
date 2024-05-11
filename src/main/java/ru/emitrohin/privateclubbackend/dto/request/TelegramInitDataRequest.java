package ru.emitrohin.privateclubbackend.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TelegramInitDataRequest(

        @NotBlank(message = "initData can't be blank")
        String initData
) {
}
