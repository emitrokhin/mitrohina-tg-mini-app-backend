package ru.emitrohin.privateclubbackend.dto.request.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

//TODO при обновлении coverImage может отсутствовать. В сервисе должна быть обработка
public record UpdateCourseRequest(

        @NotBlank
        String title,

        @NotBlank
        String description,

        boolean published,

        @NotNull
        MultipartFile coverImage
){ }