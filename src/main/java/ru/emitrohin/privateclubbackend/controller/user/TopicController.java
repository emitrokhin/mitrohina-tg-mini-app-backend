package ru.emitrohin.privateclubbackend.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.emitrohin.privateclubbackend.dto.response.topic.TopicDetailsResponse;
import ru.emitrohin.privateclubbackend.model.EnrollmentStatus;
import ru.emitrohin.privateclubbackend.service.TopicService;

import java.util.Optional;
import java.util.UUID;

@RequestMapping("/my")
@RequiredArgsConstructor
@RestController
public class TopicController {

    private final TopicService topicService;

    @GetMapping("topics/{id}")
    //TODO не взят статус enrolled в подкастах
    public ResponseEntity<TopicDetailsResponse> getTopicDetails(@PathVariable("id") UUID topicId) {
        return topicService.findByIdAndPublishedTrue(topicId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("topics/{id}/enroll")
    public ResponseEntity<Void> enrollInTopic(@PathVariable("id") UUID topicId) {
        topicService.setTopicEnrollmentStatus(topicId, EnrollmentStatus.ACTIVE);
        return ResponseEntity.ok().build();
    }

    //TODO реализация завершения курса на основе события завершение последней темы
    //TODO реализация старта курса на основе события старт материала
    //TODO вести учет завершенных тем
}