package ru.emitrohin.privateclubbackend.dto.request.material;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

//TODO при обновлении coverImage mediaFile может отсутствовать
public record MaterialUpdateRequest(

        @NotNull
        UUID topicId,

        @NotBlank
        String title,

        @Positive
        Integer duration,

        boolean published,

        @NotNull
        MultipartFile coverImage,

        @NotNull
        MultipartFile mediaFile) { }