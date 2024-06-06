package ru.emitrohin.privateclubbackend.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.emitrohin.privateclubbackend.model.Topic;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TopicRepository extends JpaRepository<Topic, UUID> {

    @Query("SELECT t FROM Topic t LEFT JOIN FETCH t.materials m WHERE t.id = :id AND t.published = true AND m.published = true")
    Optional<Topic> findByIdAndPublishedTrue(UUID id);
}