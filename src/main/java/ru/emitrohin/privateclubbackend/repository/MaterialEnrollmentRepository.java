package ru.emitrohin.privateclubbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.emitrohin.privateclubbackend.model.MaterialEnrollment;
import ru.emitrohin.privateclubbackend.model.TopicEnrollment;

import java.util.Optional;
import java.util.UUID;

public interface MaterialEnrollmentRepository extends JpaRepository<MaterialEnrollment, UUID> {

    Optional<MaterialEnrollment> findByMaterial_IdAndUser_Id(UUID materialId, UUID userId);

}
