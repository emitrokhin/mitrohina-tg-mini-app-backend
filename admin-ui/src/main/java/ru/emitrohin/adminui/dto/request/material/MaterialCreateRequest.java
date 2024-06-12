package ru.emitrohin.adminui.dto.request.material;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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