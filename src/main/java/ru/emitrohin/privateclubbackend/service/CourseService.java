package ru.emitrohin.privateclubbackend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.emitrohin.privateclubbackend.dto.CourseRequest;
import ru.emitrohin.privateclubbackend.dto.CourseResponse;
import ru.emitrohin.privateclubbackend.mapper.CourseMapper;
import ru.emitrohin.privateclubbackend.model.Course;
import ru.emitrohin.privateclubbackend.repository.CourseRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    public Optional<CourseResponse> findById(UUID id) {
        return courseRepository.findById(id).map(CourseMapper.INSTANCE::toResponse);
    }

    public List<CourseResponse> findAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(CourseMapper.INSTANCE::toResponse)
                .collect(Collectors.toList());
    }

    public CourseResponse save(CourseRequest courseRequest) {
        Course newCourse = CourseMapper.INSTANCE.toCourse(courseRequest);
        Course savedCourse = courseRepository.save(newCourse);
        return CourseMapper.INSTANCE.toResponse(savedCourse);
    }

    @Transactional
    public Optional<CourseResponse> updateCourse(UUID id, CourseRequest courseRequest) {
        return courseRepository.findById(id)
                .map(course -> courseRepository.save(CourseMapper.INSTANCE.toCourse(courseRequest)))
                .map(CourseMapper.INSTANCE::toResponse);
    }

    @Transactional
    public void deleteById(UUID courseId) {
        courseRepository.findById(courseId).ifPresent(course -> {
            if (!course.getEnrollments().isEmpty()) {
                throw new IllegalStateException("Cannot delete course with active enrollments");
            }
            courseRepository.deleteById(courseId);  // Каскадное удаление
        });
    }
}