package ru.emitrohin.privateclubbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.emitrohin.privateclubbackend.model.CourseEnrollment;

import java.util.Optional;
import java.util.UUID;

public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, UUID> {

    Optional<CourseEnrollment> findByCourse_IdAndUser_Id(UUID courseId, UUID userId);
}
