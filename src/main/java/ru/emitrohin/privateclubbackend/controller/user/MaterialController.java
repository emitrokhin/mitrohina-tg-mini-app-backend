package ru.emitrohin.privateclubbackend.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.emitrohin.privateclubbackend.dto.MaterialResponse;
import ru.emitrohin.privateclubbackend.service.MaterialService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequestMapping("api/my/")
@RequiredArgsConstructor
@RestController
public class MaterialController {

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
}