package ru.emitrohin.privateclubbackend.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.emitrohin.privateclubbackend.dto.request.material.MaterialCreateRequest;
import ru.emitrohin.privateclubbackend.dto.request.material.MaterialUpdateRequest;
import ru.emitrohin.privateclubbackend.dto.response.material.AdminMaterialResponse;
import ru.emitrohin.privateclubbackend.service.MaterialService;
import ru.emitrohin.privateclubbackend.util.S3Utils;

import java.util.Optional;
import java.util.UUID;

@RequestMapping("/admin/materials")
@RequiredArgsConstructor
@RestController
//TODO где то вести учет файлов
public class AdminMaterialController {

    private final Logger logger = LoggerFactory.getLogger(AdminCourseController.class);

    private final MaterialService materialService;

    private final S3Utils s3Utils;

    @GetMapping("/{id}")
    public ResponseEntity<AdminMaterialResponse> getMaterial(@PathVariable("id") UUID id) {
        Optional<AdminMaterialResponse> data = materialService.findById(id);
        return data
                .map(value -> new ResponseEntity<>(value, HttpStatus.CREATED))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping()
    public ResponseEntity<AdminMaterialResponse> createMaterial(@Valid @RequestBody MaterialCreateRequest createRequest) {
        String coverObjectKey = null;
        String mediaObjectKey = null;
        try {
            coverObjectKey = s3Utils.uploadPublicFile(createRequest.coverImage());
            mediaObjectKey = s3Utils.uploadPrivateFile(createRequest.mediaFile());
            var newMaterial = materialService.createMaterial(createRequest, coverObjectKey, mediaObjectKey);
            return ResponseEntity.status(HttpStatus.CREATED).body(newMaterial);
        } catch (Exception e) {
            deleteFile(coverObjectKey);
            deleteFile(mediaObjectKey);

            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    //TODO при обновлении материалов старые удалить
    public ResponseEntity<AdminMaterialResponse> updateMaterial(@PathVariable("id") UUID materialId,
                                                                @Valid @RequestBody MaterialUpdateRequest updateRequest) {
        String coverObjectKey = null;
        String mediaObjectKey = null;
        try {
            coverObjectKey = s3Utils.uploadPublicFile(updateRequest.coverImage());
            mediaObjectKey = s3Utils.uploadPrivateFile(updateRequest.mediaFile());
            return materialService.updateMaterial(materialId, updateRequest, coverObjectKey, mediaObjectKey)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            deleteFile(coverObjectKey);
            deleteFile(mediaObjectKey);

            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}/publish")
    public ResponseEntity<AdminMaterialResponse> updatePublishedStatus(@PathVariable("id") UUID materialId,
                                                                       @RequestParam boolean published) {
        return materialService.updatePublishedStatus(materialId, published)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    //TODO delete is s3

    public ResponseEntity<Void> deleteMaterial(@PathVariable("id") UUID id) {
        materialService.deleteById(id);
        return ResponseEntity.noContent().build(); //No content is RESTful
    }

    private void deleteFile(String objectKey) {
        if (objectKey != null) {
            try {
                s3Utils.deleteFile(objectKey);
            } catch (Exception ex) {
                logger.warn("Can't delete file with objectKey {}. Reason: {}", objectKey, ex.getMessage());
            }
        }
    }
}