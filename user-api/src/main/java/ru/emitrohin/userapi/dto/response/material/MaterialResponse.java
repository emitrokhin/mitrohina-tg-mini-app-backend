package ru.emitrohin.userapi.dto.response.material;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;

import java.util.UUID;

public record MaterialResponse(

        @NotNull
        UUID id,

        @NotNull
        UUID topicId,

        @NotBlank
        String title,

        @URL
        String coverUrl,

        @URL
        String mediaUrl) { }