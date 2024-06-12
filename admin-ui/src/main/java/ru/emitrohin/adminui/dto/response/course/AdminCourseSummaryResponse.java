package ru.emitrohin.adminui.dto.response.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;

import java.util.UUID;

public record AdminCourseSummaryResponse(

        @NotNull
        UUID id,

        @NotBlank
        String title,

        @NotBlank
        String description,

        @URL
        String coverUrl,

        boolean published) {

}