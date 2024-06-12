package ru.emitrohin.userapi.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.emitrohin.data.model.Course;
import ru.emitrohin.data.model.EnrollmentStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {

    @EntityGraph(attributePaths = {"topics"})
    Optional<Course> findById(UUID id);

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.topics t WHERE c.id = :id AND c.published = true AND t.published = true")
    Optional<Course> findByIdAndPublishedTrue(UUID id);

    List<Course> findByPublishedTrue();

    // Метод для поиска всех курсов пользователя со статусом EnrollmentStatus.ACTIVE
    @Query("SELECT c FROM Course c " +
            "JOIN c.enrollments e " +
            "WHERE e.user.id = :userId " +
            "AND e.status = :status")
    List<Course> findEnrolledCoursesByUserIdAndStatus(UUID userId, EnrollmentStatus status);
}