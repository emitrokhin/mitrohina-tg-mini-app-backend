package ru.emitrohin.privateclubbackend.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.emitrohin.privateclubbackend.dto.request.course.CreateCourseRequest;
import ru.emitrohin.privateclubbackend.dto.request.course.UpdateCourseRequest;
import ru.emitrohin.privateclubbackend.dto.response.course.AdminCourseDetailsResponse;
import ru.emitrohin.privateclubbackend.dto.response.course.AdminCourseSummaryResponse;
import ru.emitrohin.privateclubbackend.service.CourseService;
import ru.emitrohin.privateclubbackend.util.S3Utils;

import java.util.*;

//TODO рассмотреть возможность предварительно загружать данные и получать objectKey на фронте
//TODO отслеживать objectKey, а то можно наплодить несвязанный мусор
//TODO рассмотреть возможность json patch
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/courses")
public class AdminCourseController {

    private final Logger logger = LoggerFactory.getLogger(AdminCourseController.class);

    private final CourseService courseService;

    private final S3Utils s3Utils;

    @GetMapping("/{id}")
    public ResponseEntity<AdminCourseDetailsResponse> getCourse(@PathVariable("id") UUID id) {
        return courseService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<AdminCourseSummaryResponse>> getAllCourses() {
        var courses = courseService.findAllCourses();
        return ResponseEntity.ok(courses);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdminCourseSummaryResponse> createCourse(@Valid @RequestBody CreateCourseRequest request) {
        String objectKey = null;
        try {
            objectKey = s3Utils.uploadPublicFile(request.coverImage());
            var newCourse = courseService.createCourse(objectKey, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(newCourse);
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
    //TODO при обновлении материалов старые удалить
    public ResponseEntity<AdminCourseSummaryResponse> updateCourseSummary(@PathVariable("id") UUID id,
                                                                          @Valid @RequestBody UpdateCourseRequest request) {
        String objectKey = null;
        try {
            objectKey = s3Utils.uploadPublicFile(request.coverImage());
            return courseService.updateCourse(id, objectKey, request)
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
        } finally {
            findCourseObjectKeyAndDelete(id);
        }
    }

    @PutMapping("/{id}/publish")
    public ResponseEntity<AdminCourseSummaryResponse> updatePublishedStatus(@PathVariable("id") UUID id, @RequestParam boolean published) {
        return courseService.updatePublishedStatus(id, published)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable("id") UUID id) {
        courseService.deleteById(id);
        findCourseObjectKeyAndDelete(id);
        return ResponseEntity.noContent().build();
    }

    private void findCourseObjectKeyAndDelete(UUID courseId) {
        courseService.getCourseObjectKey(courseId).ifPresentOrElse(
                s3Utils::deleteFile,
                () -> logger.warn("Can't delete objectKey file for course {}", courseId));
    }
}