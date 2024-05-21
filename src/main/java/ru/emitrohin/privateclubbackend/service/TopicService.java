package ru.emitrohin.privateclubbackend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.emitrohin.privateclubbackend.dto.mapper.EnrollmentMapper;
import ru.emitrohin.privateclubbackend.dto.request.topic.TopicCreateRequest;
import ru.emitrohin.privateclubbackend.dto.request.topic.TopicUpdateRequest;
import ru.emitrohin.privateclubbackend.dto.response.EnrollmentResponse;
import ru.emitrohin.privateclubbackend.dto.response.topic.AdminTopicDetailsResponse;
import ru.emitrohin.privateclubbackend.dto.response.topic.AdminTopicSummaryResponse;
import ru.emitrohin.privateclubbackend.dto.response.topic.TopicDetailsResponse;
import ru.emitrohin.privateclubbackend.dto.mapper.TopicMapper;
import ru.emitrohin.privateclubbackend.model.EnrollmentStatus;
import ru.emitrohin.privateclubbackend.model.Topic;
import ru.emitrohin.privateclubbackend.model.TopicEnrollment;
import ru.emitrohin.privateclubbackend.repository.CourseRepository;
import ru.emitrohin.privateclubbackend.repository.TopicEnrollmentRepository;
import ru.emitrohin.privateclubbackend.repository.TopicRepository;
import ru.emitrohin.privateclubbackend.repository.UserRepository;
import ru.emitrohin.privateclubbackend.util.UserUtils;

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

    public Optional<TopicDetailsResponse> findByIdAndPublishedTrue(UUID topicId) {
        return topicRepository.findByIdAndPublishedTrue(topicId).map(topicMapper::toTopicDetailsResponse);
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
