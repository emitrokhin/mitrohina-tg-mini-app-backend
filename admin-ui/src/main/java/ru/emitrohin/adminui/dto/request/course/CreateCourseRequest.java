package ru.emitrohin.adminui.dto.request.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record CreateCourseRequest(

        @NotBlank
        String title,

        @NotBlank
        String description,

        @NotNull
        MultipartFile coverImage) { }