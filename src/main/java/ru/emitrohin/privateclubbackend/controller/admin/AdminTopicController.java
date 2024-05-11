package ru.emitrohin.privateclubbackend.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.emitrohin.privateclubbackend.dto.TopicRequest;
import ru.emitrohin.privateclubbackend.dto.TopicResponse;
import ru.emitrohin.privateclubbackend.service.TopicService;

import java.util.*;

@RequestMapping("api/admin")
@RequiredArgsConstructor
@RestController
public class AdminTopicController {

    private final TopicService topicService;

    @GetMapping("topics/{id}")
    public ResponseEntity<TopicResponse> getTopic(@PathVariable("id") UUID id) {
        Optional<TopicResponse> topic = topicService.findById(id);
        return topic
                .map(value -> new ResponseEntity<>(value, HttpStatus.CREATED))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("courses/{course-id}/topics")
    public ResponseEntity<List<TopicResponse>> getTopics(@PathVariable("course-id") UUID courseId) {
        List<TopicResponse> courses = topicService.findAllByCourseId(courseId);
        return ResponseEntity.ok(courses);
    }

    @PostMapping("topics/")
    public ResponseEntity<TopicResponse> createTopic(@RequestBody TopicRequest topicRequest) {
        TopicResponse newTopicResponse = topicService.save(topicRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(newTopicResponse);
    }

    @PutMapping("topics/{id}")
    public ResponseEntity<TopicResponse> updateTopic(@PathVariable("id") UUID id,
                                                     @Valid @RequestBody TopicRequest topicRequest) {
        Optional<TopicResponse> updatedTopic = topicService.updateCourse(id, topicRequest);
        return updatedTopic
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("topics/{id}")
    public ResponseEntity<HttpStatus> deleteCourseTopic(@PathVariable("id") UUID id) {
        topicService.deleteById(id);
        return ResponseEntity.noContent().build(); //No content is RESTful
    }
}