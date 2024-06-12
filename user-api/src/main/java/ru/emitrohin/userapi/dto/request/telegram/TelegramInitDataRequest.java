package ru.emitrohin.userapi.dto.request.telegram;

import jakarta.validation.constraints.NotBlank;

public record TelegramInitDataRequest(

        @NotBlank(message = "initData can't be blank")
        String initData
) {
}
