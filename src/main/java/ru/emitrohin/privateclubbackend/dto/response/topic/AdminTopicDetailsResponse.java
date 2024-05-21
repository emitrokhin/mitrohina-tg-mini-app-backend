package ru.emitrohin.privateclubbackend.dto.response.topic;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;
import ru.emitrohin.privateclubbackend.model.Material;

import java.util.List;
import java.util.UUID;

public record AdminTopicDetailsResponse(

        @NotNull
        UUID id,

        @NotNull
        UUID courseId,

        @NotBlank
        String title,

        @NotBlank
        String description,

        @URL(message = "Cover URL must be a valid URL")
        String coverUrl,

        boolean published,

        List<MaterialSummaryResponse> materials) {
        /**
         * DTO for {@link Material}
         */
        public record MaterialSummaryResponse(@NotNull UUID id, @NotNull String title) {
        }
}