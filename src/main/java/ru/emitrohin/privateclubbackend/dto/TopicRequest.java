package ru.emitrohin.privateclubbackend.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import ru.emitrohin.privateclubbackend.model.Material;

import java.util.List;
import java.util.UUID;

public record TopicRequest(

        UUID id,

        UUID courseId,

        @NotBlank
        String title,

        @NotBlank
        String description,

        @URL(message = "Cover URL must be a valid URL")
        String coverUrl) { }