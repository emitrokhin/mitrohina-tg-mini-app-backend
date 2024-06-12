package ru.emitrohin.adminui.dto.response.topic;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;
import ru.emitrohin.data.model.Material;

import java.util.List;
import java.util.UUID;

public record TopicDetailsResponse(

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

        List<MaterialSummaryResponse> materials) {
        /**
         * DTO for {@link Material}
         */
        public record MaterialSummaryResponse(@NotNull UUID id, @NotBlank String title, @NotNull Integer duration) {
        }
}