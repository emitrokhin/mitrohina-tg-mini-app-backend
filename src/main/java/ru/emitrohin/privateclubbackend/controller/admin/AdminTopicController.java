package ru.emitrohin.privateclubbackend.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.emitrohin.privateclubbackend.dto.request.topic.TopicUpdateRequest;
import ru.emitrohin.privateclubbackend.dto.response.course.AdminCourseSummaryResponse;
import ru.emitrohin.privateclubbackend.dto.response.topic.AdminTopicDetailsResponse;
import ru.emitrohin.privateclubbackend.dto.response.topic.AdminTopicSummaryResponse;
import ru.emitrohin.privateclubbackend.dto.response.topic.TopicDetailsResponse;
import ru.emitrohin.privateclubbackend.dto.request.topic.TopicCreateRequest;
import ru.emitrohin.privateclubbackend.service.TopicService;
import ru.emitrohin.privateclubbackend.util.S3Utils;

import java.util.*;

@RequestMapping("/admin/topics")
@RequiredArgsConstructor
@RestController
public class AdminTopicController {

    private final Logger logger = LoggerFactory.getLogger(AdminCourseController.class);

    private final TopicService topicService;

    private final S3Utils s3Utils;

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
            objectKey = s3Utils.uploadPublicFile(request.coverImage());
            var newTopic = topicService.createTopic(objectKey, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(newTopic);
        } catch (Exception e) {
            if (objectKey != null) {
                try {
                    s3Utils.deleteFile(objectKey);
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
            objectKey = s3Utils.uploadPublicFile(updateRequest.coverImage());
            return topicService.updateTopic(topicId, objectKey, updateRequest)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            if (objectKey != null) {
                try {
                    s3Utils.deleteFile(objectKey);
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
    //TODO delete is s3

    public ResponseEntity<HttpStatus> deleteTopic(@PathVariable("id") UUID topicId) {
        topicService.deleteById(topicId);
        return ResponseEntity.noContent().build(); //No content is RESTful
    }
}