package ru.emitrohin.privateclubbackend.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.emitrohin.privateclubbackend.dto.TopicResponse;
import ru.emitrohin.privateclubbackend.service.TopicService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequestMapping("api/my")
@RequiredArgsConstructor
@RestController
public class TopicController {

    private final TopicService topicService;

    @GetMapping("topics/{id}")
    public ResponseEntity<TopicResponse> getTopic(@PathVariable("id") UUID id) {
        Optional<TopicResponse> topic = topicService.findById(id);
        return topic
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("courses/{course-id}/topics")
    public ResponseEntity<List<TopicResponse>> getTopics(@PathVariable("course-id") UUID courseId) {
        List<TopicResponse> courses = topicService.findAllByCourseId(courseId);
        return ResponseEntity.ok(courses);
    }
}