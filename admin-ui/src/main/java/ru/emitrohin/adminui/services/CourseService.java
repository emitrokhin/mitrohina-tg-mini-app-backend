package ru.emitrohin.adminui.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.emitrohin.adminui.dto.mapper.CourseMapper;
import ru.emitrohin.adminui.dto.mapper.EnrollmentMapper;
import ru.emitrohin.adminui.dto.request.course.CreateCourseRequest;
import ru.emitrohin.adminui.dto.request.course.UpdateCourseRequest;
import ru.emitrohin.adminui.dto.response.EnrollmentResponse;
import ru.emitrohin.adminui.dto.response.course.AdminCourseDetailsResponse;
import ru.emitrohin.adminui.dto.response.course.AdminCourseSummaryResponse;
import ru.emitrohin.adminui.dto.response.course.CourseDetailsResponse;
import ru.emitrohin.adminui.dto.response.course.CourseSummaryResponse;
import ru.emitrohin.adminui.repository.CourseEnrollmentRepository;
import ru.emitrohin.adminui.repository.CourseRepository;
import ru.emitrohin.adminui.repository.UserRepository;
import ru.emitrohin.adminui.util.UserUtils;
import ru.emitrohin.data.model.Course;
import ru.emitrohin.data.model.CourseEnrollment;
import ru.emitrohin.data.model.EnrollmentStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    private final UserRepository userRepository;

    private final CourseEnrollmentRepository courseEnrollmentRepository;

    private final CourseMapper courseMapper;

    private final EnrollmentMapper enrollmentMapper;

    public Optional<AdminCourseDetailsResponse> findById(UUID id) {
        return courseRepository.findById(id).map(courseMapper::toAdminCourseDetailsResponse);
    }

    public Optional<CourseDetailsResponse> findByIdAndPublishedTrue(UUID id) {
        return courseRepository.findByIdAndPublishedTrue(id).map(courseMapper::toCourseDetailsResponse);
    }

    public List<CourseSummaryResponse> findAllPublishedCourses() {
        return courseRepository.findByPublishedTrue()
                .stream()
                .map(courseMapper::toCourseSummaryResponse)
                .collect(Collectors.toList());
    }

    public List<AdminCourseSummaryResponse> findAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(courseMapper::toAdminCourseSummaryResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    //TODO нельзя зачислиться на неопубликованный курс
    public EnrollmentResponse setCourseEnrollmentStatus(UUID courseId, EnrollmentStatus status) {
        var course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId)); //TODO свой тип исключения?

        var userId = UserUtils.getCurrentUserId();
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId)); //TODO свой тип исключения?

        var courseEnrollment = courseEnrollmentRepository.findByCourse_IdAndUser_Id(courseId, userId)
                .orElseGet(() -> {
                    var enrollment = new CourseEnrollment();
                    enrollment.setCourse(course);
                    enrollment.setUser(user);
                    enrollment.setStatus(status);
                    return courseEnrollmentRepository.save(enrollment);
                });

       return enrollmentMapper.fromCourseEnrollment(courseEnrollment);
    }

    public AdminCourseSummaryResponse createCourse(String objectKey, CreateCourseRequest request) {
        var newCourse = courseMapper.toCourse(request, objectKey);
        var savedCourse = courseRepository.save(newCourse);
        return courseMapper.toAdminCourseSummaryResponse(savedCourse);
    }

    @Transactional
    public Optional<AdminCourseSummaryResponse> updateCourse(UUID courseId, String objectKey, UpdateCourseRequest request) {
        return courseRepository.findById(courseId)
                .map(course -> courseRepository.save(courseMapper.toCourseWithIdAndObjectKey(request, courseId, objectKey)))
                .map(courseMapper::toAdminCourseSummaryResponse);
    }

    @Transactional
    public void deleteById(UUID courseId) {
        courseRepository.findById(courseId).ifPresent(course -> {
            if (!course.getEnrollments().isEmpty()) {
                throw new IllegalStateException("Cannot delete course with active enrollment. Course id: " + courseId);
            }
            courseRepository.deleteById(courseId);  //TODO Каскадное удаление надо бы запретить, пока есть курсы
        });
    }

    @Transactional
    //TODO если снимается с публикации курс, то и каскадно все темы и материалы
    public Optional<AdminCourseSummaryResponse> updatePublishedStatus(UUID courseId, boolean published) {
        return courseRepository.findById(courseId)
                .map(course -> {
                    course.setPublished(published);
                    return courseRepository.save(course);
                })
                .map(courseMapper::toAdminCourseSummaryResponse);
    }

    public Optional<String> getCourseObjectKey(UUID courseId) {
        return courseRepository.findById(courseId).map(Course::getCoverObjectKey);
    }
}