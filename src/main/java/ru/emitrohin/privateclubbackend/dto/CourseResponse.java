package ru.emitrohin.privateclubbackend.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import ru.emitrohin.privateclubbackend.model.Topic;

import java.util.List;
import java.util.UUID;

public record CourseResponse(

        UUID id,

        @NotBlank
        String title,

        @NotBlank
        String description,

        @URL(message = "Cover URL must be a valid URL")
        String coverUrl,

        List<TopicResponse> topics) {

        /**
         * DTO for {@link Topic}
         */
        public record TopicResponse(UUID id, String title, String description, String coverUrl) {
        }
}