package ru.emitrohin.userapi.dto.response.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;
import ru.emitrohin.data.model.Topic;

import java.util.List;
import java.util.UUID;

public record CourseDetailsResponse(

        @NotNull
        UUID id,

        @NotBlank
        String title,

        @NotBlank
        String description,

        @URL(message = "Cover URL must be a valid URL")
        String coverUrl,

        List<TopicSummary> topics) {

        /**
         * DTO for {@link Topic}
         */
        public record TopicSummary(UUID id, String title, String description, String coverUrl) {
        }
}