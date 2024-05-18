package ru.emitrohin.privateclubbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.emitrohin.privateclubbackend.model.TopicEnrollment;

import java.util.Optional;
import java.util.UUID;

public interface TopicEnrollmentRepository extends JpaRepository<TopicEnrollment, UUID> {

    Optional<TopicEnrollment> findByTopic_IdAndUser_Id(UUID topicId, UUID userId);
}
