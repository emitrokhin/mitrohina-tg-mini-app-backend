package ru.emitrohin.privateclubbackend.dto.request.topic;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record TopicUpdateRequest(

        @NotBlank
        String title,

        @NotBlank
        String description,

        boolean published,

        @NotNull
        MultipartFile coverImage) { }