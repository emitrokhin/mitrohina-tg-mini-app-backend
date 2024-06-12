package ru.emitrohin.userapi.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.emitrohin.data.model.Course;
import ru.emitrohin.data.model.CourseEnrollment;
import ru.emitrohin.data.model.EnrollmentStatus;
import ru.emitrohin.userapi.repository.CourseEnrollmentRepository;
import ru.emitrohin.userapi.repository.CourseRepository;
import ru.emitrohin.userapi.repository.UserRepository;
import ru.emitrohin.userapi.util.UserUtils;
import ru.emitrohin.userapi.dto.mapper.CourseMapper;
import ru.emitrohin.userapi.dto.mapper.EnrollmentMapper;
import ru.emitrohin.userapi.dto.response.EnrollmentResponse;
import ru.emitrohin.userapi.dto.response.course.CourseDetailsResponse;
import ru.emitrohin.userapi.dto.response.course.CourseSummaryResponse;


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


    public Optional<CourseDetailsResponse> findByIdAndPublishedTrue(UUID id) {
        return courseRepository.findByIdAndPublishedTrue(id).map(courseMapper::toCourseDetailsResponse);
    }

    public List<CourseSummaryResponse> findAllPublishedCourses() {
        return courseRepository.findByPublishedTrue()
                .stream()
                .map(courseMapper::toCourseSummaryResponse)
                .collect(Collectors.toList());
    }

    public List<CourseSummaryResponse> getMyEnrolledCourses() {
        return getCoursesWithEnrollmentStatus(EnrollmentStatus.ACTIVE);
    }

    public List<CourseSummaryResponse> getMyCompletedCourses() {
        return getCoursesWithEnrollmentStatus(EnrollmentStatus.COMPLETED);
    }

    private List<CourseSummaryResponse> getCoursesWithEnrollmentStatus(EnrollmentStatus status) {
        return courseRepository.findEnrolledCoursesByUserIdAndStatus(UserUtils.getCurrentUserId(), status)
                .stream()
                .map(courseMapper::toCourseSummaryResponse)
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

    public Optional<String> getCourseObjectKey(UUID courseId) {
        return courseRepository.findById(courseId).map(Course::getCoverObjectKey);
    }
}