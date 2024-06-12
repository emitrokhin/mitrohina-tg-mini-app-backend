package ru.emitrohin.adminui.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.emitrohin.data.model.MaterialEnrollment;

import java.util.Optional;
import java.util.UUID;

public interface MaterialEnrollmentRepository extends JpaRepository<MaterialEnrollment, UUID> {

    Optional<MaterialEnrollment> findByMaterial_IdAndUser_Id(UUID materialId, UUID userId);

}
