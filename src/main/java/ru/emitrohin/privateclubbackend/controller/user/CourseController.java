package ru.emitrohin.privateclubbackend.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.emitrohin.privateclubbackend.dto.response.EnrollmentResponse;
import ru.emitrohin.privateclubbackend.dto.response.course.CourseDetailsResponse;
import ru.emitrohin.privateclubbackend.dto.response.course.CourseSummaryResponse;
import ru.emitrohin.privateclubbackend.model.EnrollmentStatus;
import ru.emitrohin.privateclubbackend.service.CourseService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/my/courses")
//TODO не берется в учет published статус во всех методах
public class CourseController {

    private final CourseService courseService;

    @GetMapping("/{id}")
    //TODO не взят статус enrolled в темах
    public ResponseEntity<CourseDetailsResponse> getCourseDetails(@PathVariable("id") UUID topicId) {
        return courseService.findByIdAndPublishedTrue(topicId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<CourseSummaryResponse>> getAllPublishedCourses() {
        var courses = courseService.findAllPublishedCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/enrolled")
    public ResponseEntity<List<CourseSummaryResponse>> getMyEnrolledCourses() {
        var courses = courseService.getMyEnrolledCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/completed")
    public ResponseEntity<List<CourseSummaryResponse>> getMyCompletedCourses() {
        var courses = courseService.getMyCompletedCourses();
        return ResponseEntity.ok(courses);
    }

    @PostMapping("/{id}/enroll")
    public ResponseEntity<EnrollmentResponse> enrollInCourse(@PathVariable("id") UUID courseId) {
        var response = courseService.setCourseEnrollmentStatus(courseId, EnrollmentStatus.ACTIVE);
        return ResponseEntity.ok(response);
    }

    //TODO реализация завершения курса на основе события

    //TODO реализация старта курса на основе события

}