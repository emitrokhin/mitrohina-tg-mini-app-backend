package ru.emitrohin.adminui.dto.response.topic;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;

import java.util.UUID;

public record TopicSummaryResponse(

        @NotNull
        UUID id,

        @NotNull
        UUID courseId,

        @NotBlank
        String title,

        @NotBlank
        String description,

        @URL(message = "Cover URL must be a valid URL")
        String coverUrl) {
}
