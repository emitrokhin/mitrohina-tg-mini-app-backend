package ru.emitrohin.privateclubbackend.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

import java.util.UUID;

public record MaterialResponse(

        UUID id,

        UUID topicId,

        @NotBlank
        String title,

        @URL(message = "Media URL must be a valid URL")
        String mediaUrl) { }