package ru.emitrohin.adminui.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.emitrohin.data.model.Topic;

import java.util.Optional;
import java.util.UUID;

public interface TopicRepository extends JpaRepository<Topic, UUID> {

    @Query("SELECT t FROM Topic t LEFT JOIN FETCH t.materials m WHERE t.id = :id AND t.published = true AND m.published = true")
    Optional<Topic> findByIdAndPublishedTrue(UUID id);
}