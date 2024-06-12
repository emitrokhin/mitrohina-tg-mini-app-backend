package ru.emitrohin.userapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.emitrohin.data.model.Material;

import java.util.Optional;
import java.util.UUID;

public interface MaterialRepository extends JpaRepository<Material, UUID> {

    Optional<Material> findByIdAndPublishedTrue(UUID materialId);
}