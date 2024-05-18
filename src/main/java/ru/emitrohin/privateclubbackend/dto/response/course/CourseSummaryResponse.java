package ru.emitrohin.privateclubbackend.dto.response.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;

import java.util.UUID;

public record CourseSummaryResponse(

        @NotNull
        UUID id,

        @NotBlank
        String title,

        @NotBlank
        String description,

        @URL(message = "Cover URL must be a valid URL")
        String coverUrl) {

}