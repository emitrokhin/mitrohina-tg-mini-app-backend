package ru.emitrohin.privateclubbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.emitrohin.privateclubbackend.model.Material;

import java.util.List;
import java.util.UUID;

public interface MaterialRepository extends JpaRepository<Material, UUID> {

    List<Material> findByTopicId(UUID topicId);
}