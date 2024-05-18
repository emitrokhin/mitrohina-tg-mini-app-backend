package ru.emitrohin.privateclubbackend.dto.request.material;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.hibernate.validator.constraints.URL;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record MaterialCreateRequest(

        @NotNull
        UUID materialId,

        @NotNull
        UUID topicId,

        @NotBlank
        String title,

        @Positive
        Integer duration,

        @NotNull
        MultipartFile coverImage,

        @NotNull
        MultipartFile mediaFile) { }