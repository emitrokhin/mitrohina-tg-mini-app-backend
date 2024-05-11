package ru.emitrohin.privateclubbackend.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.emitrohin.privateclubbackend.dto.CourseRequest;
import ru.emitrohin.privateclubbackend.dto.CourseResponse;
import ru.emitrohin.privateclubbackend.service.CourseService;

import java.util.*;

@RestController
@RequiredArgsConstructor
//TODO перенести API в свойвства
@RequestMapping("api/admin/courses")
public class AdminCourseController {

    private final CourseService courseService;

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourse(@PathVariable("id") UUID id) {
        Optional<CourseResponse> course = courseService.findById(id);
        return course
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        List<CourseResponse> courses = courseService.findAllCourses();
        return ResponseEntity.ok(courses);
    }

    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CourseRequest courseRequest) {
        CourseResponse newCourse = courseService.save(courseRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCourse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseResponse> updateCourse(@PathVariable("id") UUID id,
                                                       @Valid @RequestBody CourseRequest courseRequest) {
        Optional<CourseResponse> updatedCourse = courseService.updateCourse(id, courseRequest);
        return updatedCourse
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable("id") UUID id) {
        courseService.deleteById(id);
        return ResponseEntity.noContent().build(); //No content is RESTful
    }
}