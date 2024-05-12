package ru.emitrohin.privateclubbackend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.emitrohin.privateclubbackend.dto.TopicRequest;
import ru.emitrohin.privateclubbackend.dto.TopicResponse;
import ru.emitrohin.privateclubbackend.mapper.TopicMapper;
import ru.emitrohin.privateclubbackend.model.Topic;
import ru.emitrohin.privateclubbackend.repository.MaterialRepository;
import ru.emitrohin.privateclubbackend.repository.TopicRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;

    public Optional<TopicResponse> findById(UUID courseTopicId) {
        return topicRepository.findById(courseTopicId).map(TopicMapper.INSTANCE::toResponse);
    }

    public List<TopicResponse> findAllByCourseId(UUID courseId) {
        return topicRepository.findByCourseId(courseId)
                .stream()
                .map(TopicMapper.INSTANCE::toResponse)
                .collect(Collectors.toList());
    }

    public TopicResponse save(TopicRequest topicRequest) {
        Topic newTopic = TopicMapper.INSTANCE.toTopic(topicRequest);
        Topic savedTopic = topicRepository.save(newTopic);
        return TopicMapper.INSTANCE.toResponse(savedTopic);
    }

    @Transactional
    public Optional<TopicResponse> updateCourse(UUID id, TopicRequest topicRequest) {
        return topicRepository.findById(id)
                .map(topic -> topicRepository.save(TopicMapper.INSTANCE.toTopic(topicRequest)))
                .map(TopicMapper.INSTANCE::toResponse);
    }

    @Transactional
    public void deleteById(UUID id) {
        topicRepository.findById(id).ifPresent(topic -> {
            if (!topic.getEnrollments().isEmpty()) {
                throw new IllegalStateException("Cannot delete topic with active enrollments");
            }

            topicRepository.deleteById(id);
        });
    }
}
