package ru.emitrohin.privateclubbackend.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.emitrohin.privateclubbackend.dto.CourseResponse;
import ru.emitrohin.privateclubbackend.service.CourseService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
//TODO перенести API в свойвства
@RequestMapping("api/my/courses")
public class CourseController {

    private final CourseService courseService;

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourse(@PathVariable("id") UUID id) {
        Optional<CourseResponse> course = courseService.findById(id);
        return course
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    //TODO для пользователя нужна будет своя логика вывода
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        List<CourseResponse> courses = courseService.findAllCourses();
        return ResponseEntity.ok(courses);
    }

/*    @GetMapping("/enrolled")
    public ResponseEntity<List<CourseResponse>> getEnrolledCourses() {
        List<CourseResponse> courses = courseService.getEnrolledCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/completed")
    public ResponseEntity<List<CourseResponse>> getCompletedCourses() {
        List<CourseResponse> courses = courseService.getCompletedCourses();
        return ResponseEntity.ok(courses);
    }

    //TODO для выполнения действий нужен Post?
    @PostMapping("/{id}/enroll")
    public ResponseEntity<CourseResponse> enrollInCourse(@PathVariable("id") UUID id) {
        //TODO внутри должна быть логика, что возвращать контроллеру? В случае успеха? В случае неуспеха?
        Optional<CourseResponse> course = courseService.enrollInCourse(id);
        return course
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //TODO для выполнения действий нужен Post?
    @PostMapping("/{id}/finish")
    public ResponseEntity<CourseResponse> enrollInCourse(@PathVariable("id") UUID id) {
        //TODO внутри должна быть логика, что возвращать контроллеру? В случае успеха? В случае неуспеха?
        Optional<CourseResponse> course = courseService.enrollInCourse(id);
        return course
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }*/
}