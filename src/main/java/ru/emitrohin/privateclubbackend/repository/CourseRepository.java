package ru.emitrohin.privateclubbackend.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.emitrohin.privateclubbackend.model.Course;
import ru.emitrohin.privateclubbackend.model.EnrollmentStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {

    @EntityGraph(attributePaths = {"topics"})
    Optional<Course> findById(UUID id);

    @EntityGraph(attributePaths = {"topics"})
    Optional<Course> findByIdAndPublishedTrue(UUID id);

    Optional<Course> findByPublishedTrue();

    // Метод для поиска всех курсов пользователя со статусом EnrollmentStatus.ACTIVE
    @Query("SELECT c FROM Course c " +
            "JOIN c.enrollments e " +
            "WHERE e.user.id = :userId " +
            "AND e.status = :status")
    List<Course> findEnrolledCoursesByUserIdAndStatus(UUID userId, EnrollmentStatus status);
}