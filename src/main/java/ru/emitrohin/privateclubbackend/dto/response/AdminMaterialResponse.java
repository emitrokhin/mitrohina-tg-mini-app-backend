package ru.emitrohin.privateclubbackend.dto.response;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

import java.util.UUID;

public record AdminMaterialResponse(

        UUID id,

        UUID topicId,

        @NotBlank
        String title,

        @NotBlank
        String objectKey) { }