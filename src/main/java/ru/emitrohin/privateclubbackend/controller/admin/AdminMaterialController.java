package ru.emitrohin.privateclubbackend.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.emitrohin.privateclubbackend.dto.MaterialRequest;
import ru.emitrohin.privateclubbackend.dto.MaterialResponse;
import ru.emitrohin.privateclubbackend.service.MaterialService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequestMapping("api/admin/")
@RequiredArgsConstructor
@RestController
public class AdminMaterialController {

    private final MaterialService materialService;

    @GetMapping("materials/{id}")
    public ResponseEntity<MaterialResponse> getMaterial(@PathVariable("id") UUID id) {
        Optional<MaterialResponse> data = materialService.findById(id);
        return data
                .map(value -> new ResponseEntity<>(value, HttpStatus.CREATED))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("topics/{topic-id}/material")
    public ResponseEntity<List<MaterialResponse>> getAllMaterials(@PathVariable("topic-id") UUID topicId) {
        List<MaterialResponse> allByTopicId = materialService.findAllByTopicId(topicId);
        return ResponseEntity.ok(allByTopicId);
    }

    @PostMapping("topics/{topic-id}/material")
    public ResponseEntity<MaterialResponse> createMaterial(@PathVariable("topic-id") UUID topicId,
                                                           @Valid @RequestBody MaterialRequest materialRequest) {
        MaterialResponse newData = materialService.save(materialRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(newData);
    }

    @PutMapping("materials/{id}")
    public ResponseEntity<MaterialResponse> updateMaterial(@PathVariable("id") UUID id,
                                                           @Valid @RequestBody MaterialRequest materialRequest) {
        Optional<MaterialResponse> updatedData = materialService.update(id, materialRequest);
        return updatedData
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("materials/{id}")
    public ResponseEntity<Void> deleteMaterial(@PathVariable("id") UUID id) {
        materialService.deleteById(id);
        return ResponseEntity.noContent().build(); //No content is RESTful
    }
}