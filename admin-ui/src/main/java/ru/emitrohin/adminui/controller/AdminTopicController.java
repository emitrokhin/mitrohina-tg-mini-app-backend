package ru.emitrohin.adminui.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.emitrohin.adminui.dto.request.topic.TopicCreateRequest;
import ru.emitrohin.adminui.dto.request.topic.TopicUpdateRequest;
import ru.emitrohin.adminui.dto.response.topic.AdminTopicDetailsResponse;
import ru.emitrohin.adminui.dto.response.topic.AdminTopicSummaryResponse;
import ru.emitrohin.adminui.services.S3Service;
import ru.emitrohin.adminui.services.TopicService;


import java.util.*;

@RequestMapping("/admin/topics")
@RequiredArgsConstructor
@RestController
public class AdminTopicController {

    private final Logger logger = LoggerFactory.getLogger(AdminCourseController.class);

    private final TopicService topicService;

    private final S3Service s3Service;

    @GetMapping("/{id}")
    public ResponseEntity<AdminTopicDetailsResponse> getTopic(@PathVariable("id") UUID id) {
        return topicService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdminTopicSummaryResponse> createTopic(@RequestBody TopicCreateRequest request) {
        String objectKey = null;
        try {
            objectKey = s3Service.uploadPublicFile(request.coverImage());
            var newTopic = topicService.createTopic(objectKey, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(newTopic);
        } catch (Exception e) {
            if (objectKey != null) {
                try {
                    s3Service.deleteFile(objectKey);
                } catch (Exception ex) {
                    logger.warn("Can't delete file with objectKey {}. Reason: {}", objectKey, ex.getMessage());
                }
            }
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    //TODO при обновлении обложки старую удалить
    public ResponseEntity<AdminTopicSummaryResponse> updateTopicSummary(@PathVariable("id") UUID topicId,
                                                                        @Valid @RequestBody TopicUpdateRequest updateRequest) {
        String objectKey = null;
        try {
            objectKey = s3Service.uploadPublicFile(updateRequest.coverImage());
            return topicService.updateTopic(topicId, objectKey, updateRequest)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            if (objectKey != null) {
                try {
                    s3Service.deleteFile(objectKey);
                } catch (Exception ex) {
                    logger.warn("Can't delete file with objectKey {}. Reason: {}", objectKey, ex.getMessage());
                }
            }
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}/publish")
    public ResponseEntity<AdminTopicSummaryResponse> updatePublishedStatus(@PathVariable("id") UUID topicId,
                                                                           @RequestParam boolean published) {
        return topicService.updatePublishedStatus(topicId, published)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteTopic(@PathVariable("id") UUID topicId) {
        topicService.deleteById(topicId);
        return ResponseEntity.noContent().build(); //No content is RESTful
    }

    private void findTopicObjectKeyAndDelete(UUID topicId) {
        topicService.getTopicObjectKey(topicId).ifPresentOrElse(
                s3Service::deleteFile,
                () -> logger.warn("Can't delete objectKey file for topic {}", topicId));
    }
}