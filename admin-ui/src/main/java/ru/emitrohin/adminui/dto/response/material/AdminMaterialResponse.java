package ru.emitrohin.adminui.dto.response.material;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.URL;

import java.util.UUID;

public record AdminMaterialResponse(

        @NotNull
        UUID id,

        @NotNull
        UUID topicId,

        @NotBlank
        String title,

        @Positive
        Integer duration,

        boolean published,

        @URL
        String coverUrl,

        @URL
        String mediaUrl) { }