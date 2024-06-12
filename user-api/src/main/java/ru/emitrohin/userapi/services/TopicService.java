package ru.emitrohin.userapi.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.emitrohin.data.model.EnrollmentStatus;
import ru.emitrohin.data.model.Topic;
import ru.emitrohin.data.model.TopicEnrollment;

import ru.emitrohin.userapi.dto.response.EnrollmentResponse;
import ru.emitrohin.userapi.dto.response.topic.TopicDetailsResponse;
import ru.emitrohin.userapi.repository.TopicEnrollmentRepository;
import ru.emitrohin.userapi.repository.TopicRepository;
import ru.emitrohin.userapi.repository.UserRepository;
import ru.emitrohin.userapi.util.UserUtils;
import ru.emitrohin.userapi.dto.mapper.EnrollmentMapper;
import ru.emitrohin.userapi.dto.mapper.TopicMapper;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;

    private final UserRepository userRepository;

    private final TopicEnrollmentRepository topicEnrollmentRepository;

    private final TopicMapper topicMapper;

    private final EnrollmentMapper enrollmentMapper;

    public Optional<TopicDetailsResponse> findByIdAndPublishedTrue(UUID topicId) {
        return topicRepository.findByIdAndPublishedTrue(topicId).map(topicMapper::toTopicDetailsResponse);
    }

    @Transactional
    public EnrollmentResponse setTopicEnrollmentStatus(UUID topicId, EnrollmentStatus status) {
        var topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + topicId)); //TODO свой тип исключения?

        var userId = UserUtils.getCurrentUserId();
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId)); //TODO свой тип исключения?

        var topicEnrollment = topicEnrollmentRepository.findByTopic_IdAndUser_Id(topicId, userId)
                .orElseGet(() -> {
                    var enrollment = new TopicEnrollment();
                    enrollment.setTopic(topic);
                    enrollment.setUser(user);
                    enrollment.setStatus(status);
                    return topicEnrollmentRepository.save(enrollment);
                });

        return enrollmentMapper.fromTopicEnrollment(topicEnrollment);
    }

    public Optional<String> getTopicObjectKey(UUID topicId) {
        return topicRepository.findById(topicId).map(Topic::getCoverObjectKey);
    }
}
