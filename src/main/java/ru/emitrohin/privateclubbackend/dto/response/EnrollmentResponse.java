package ru.emitrohin.privateclubbackend.dto.response;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record EnrollmentResponse(

        @NotNull
        UUID enrollmentId
) { }