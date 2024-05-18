package ru.emitrohin.privateclubbackend.dto.response.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;
import ru.emitrohin.privateclubbackend.model.Topic;

import java.util.List;
import java.util.UUID;

public record AdminCourseDetailsResponse(

        @NotNull
        UUID id,

        @NotBlank
        String title,

        @NotBlank
        String description,

        @URL(message = "Cover URL must be a valid URL")
        String coverUrl,

        boolean published,

        List<TopicSummaryResponse> topics) {

        /**
         * DTO for {@link Topic}
         */
        public record TopicSummaryResponse(UUID id, String title, String description, String coverUrl) {
        }
}