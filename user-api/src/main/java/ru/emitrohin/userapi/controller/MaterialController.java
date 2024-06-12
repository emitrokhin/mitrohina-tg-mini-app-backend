package ru.emitrohin.userapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.emitrohin.data.model.EnrollmentStatus;
import ru.emitrohin.userapi.dto.mapper.MaterialMapper;
import ru.emitrohin.userapi.dto.response.EnrollmentResponse;
import ru.emitrohin.userapi.dto.response.material.MaterialResponse;
import ru.emitrohin.userapi.services.MaterialService;


import java.util.UUID;

@RequestMapping("/my/materials")
@RequiredArgsConstructor
@RestController
public class MaterialController {

    private final MaterialService materialService;

    private final MaterialMapper materialMapper;

    @GetMapping("/{id}")
    public ResponseEntity<MaterialResponse> getMaterial(@PathVariable("id") UUID materialId) {
        return materialService.findByIdAndPublishedTrue(materialId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/enroll")
    public ResponseEntity<EnrollmentResponse> enrollInMaterial(@PathVariable("id") UUID materialId) {
        var enrollment = materialService.setTopicEnrollmentStatus(materialId, EnrollmentStatus.ACTIVE);
        return ResponseEntity.ok(enrollment);
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<EnrollmentResponse> completeMaterial(@PathVariable("id") UUID materialId) {
        var enrollment = materialService.setTopicEnrollmentStatus(materialId, EnrollmentStatus.COMPLETED);
        return ResponseEntity.ok(enrollment);
    }

    //TODO реализация автоматического завершения темы на основе события завершение последнего материала
    //TODO вести учет завершенных материалов
}