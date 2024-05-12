package ru.emitrohin.privateclubbackend.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.emitrohin.privateclubbackend.dto.response.MaterialResponse;
import ru.emitrohin.privateclubbackend.mapper.MaterialMapper;
import ru.emitrohin.privateclubbackend.service.MaterialService;
import ru.emitrohin.privateclubbackend.service.S3Service;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;

import java.net.URL;
import java.util.List;
import java.util.UUID;

@RequestMapping("api/my/")
@RequiredArgsConstructor
@RestController
public class MaterialController {

    private final S3Service s3Service;
    private final MaterialService materialService;

    @GetMapping("materials/{id}")
    public ResponseEntity<MaterialResponse> getMaterial(@PathVariable("id") UUID id) {
        return materialService.findById(id)
                .map(material -> {
                    URL mediaUrl = s3Service.generatePresignedUrl(material.getMediaUrl());
                    return ResponseEntity.ok(MaterialMapper.INSTANCE.toResponse(material, mediaUrl.toExternalForm()));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("topics/{topic-id}/material")
    public ResponseEntity<List<MaterialResponse>> getAllMaterials(@PathVariable("topic-id") UUID topicId) {
        List<MaterialResponse> allByTopicId = materialService.findAllByTopicId(topicId);
        return ResponseEntity.ok(allByTopicId);
    }

    @GetMapping("test/link")
    public ResponseEntity<URL> getMaterial() {
        return ResponseEntity.ok(s3Service.generatePresignedUrl("1.Введение.mp3"));
    }
}