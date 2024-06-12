package ru.emitrohin.adminui.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.emitrohin.adminui.dto.mapper.EnrollmentMapper;
import ru.emitrohin.adminui.dto.mapper.TopicMapper;
import ru.emitrohin.adminui.dto.request.topic.TopicCreateRequest;
import ru.emitrohin.adminui.dto.request.topic.TopicUpdateRequest;
import ru.emitrohin.adminui.dto.response.EnrollmentResponse;
import ru.emitrohin.adminui.dto.response.topic.AdminTopicDetailsResponse;
import ru.emitrohin.adminui.dto.response.topic.AdminTopicSummaryResponse;
import ru.emitrohin.adminui.dto.response.topic.TopicDetailsResponse;
import ru.emitrohin.adminui.repository.CourseRepository;
import ru.emitrohin.adminui.repository.TopicEnrollmentRepository;
import ru.emitrohin.adminui.repository.TopicRepository;
import ru.emitrohin.adminui.repository.UserRepository;
import ru.emitrohin.adminui.util.UserUtils;
import ru.emitrohin.data.model.EnrollmentStatus;
import ru.emitrohin.data.model.Topic;
import ru.emitrohin.data.model.TopicEnrollment;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;

    private final CourseRepository courseRepository;

    private final UserRepository userRepository;

    private final TopicEnrollmentRepository topicEnrollmentRepository;

    private final TopicMapper topicMapper;

    private final EnrollmentMapper enrollmentMapper;

    public Optional<AdminTopicDetailsResponse> findById(UUID topicId) {
        return topicRepository.findByIdAndPublishedTrue(topicId).map(topicMapper::toAdminTopicDetailsResponse);
    }

    @Transactional
    public AdminTopicSummaryResponse createTopic(String objectKey, TopicCreateRequest createRequest) {
        var courseId = createRequest.courseId();
        var course = courseRepository.findById(courseId).
                orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId)); //TODO свой тип исключения?
        var newTopic = topicMapper.fromTopicCreateRequest(createRequest, course, objectKey);
        var savedTopic = topicRepository.save(newTopic);
        return topicMapper.toAdminTopicSummaryResponse(savedTopic);
    }

    @Transactional
    public Optional<AdminTopicSummaryResponse> updateTopic(UUID topicId, String objectKey, TopicUpdateRequest updateRequest) {
        var course = courseRepository.findById(topicId).
                orElseThrow(() -> new RuntimeException("Course not found with id: " + topicId));
        return topicRepository.findById(topicId)
                .map(topic -> topicRepository.save(topicMapper.fromTopicUpdateRequest(updateRequest, course, objectKey)))
                .map(topicMapper::toAdminTopicSummaryResponse);
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

    @Transactional
    //TODO если снимается с публикации курс, то и каскадно все темы и материалы
    public Optional<AdminTopicSummaryResponse> updatePublishedStatus(UUID topicId, boolean published) {
        return topicRepository.findById(topicId)
                .map(topic -> {
                    topic.setPublished(published);
                    return topicRepository.save(topic);
                }).map(topicMapper::toAdminTopicSummaryResponse);
    }

    public Optional<String> getTopicObjectKey(UUID topicId) {
        return topicRepository.findById(topicId).map(Topic::getCoverObjectKey);
    }
}
