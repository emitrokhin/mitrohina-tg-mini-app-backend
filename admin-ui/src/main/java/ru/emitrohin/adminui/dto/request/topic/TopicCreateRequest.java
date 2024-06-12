package ru.emitrohin.adminui.dto.request.topic;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record TopicCreateRequest(

        @NotNull
        UUID courseId,

        @NotBlank
        String title,

        @NotBlank
        String description,

        @NotNull
        MultipartFile coverImage) { }