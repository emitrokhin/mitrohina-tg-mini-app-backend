package ru.emitrohin.privateclubbackend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.emitrohin.privateclubbackend.dto.mapper.EnrollmentMapper;
import ru.emitrohin.privateclubbackend.dto.mapper.MaterialMapper;
import ru.emitrohin.privateclubbackend.dto.request.material.MaterialCreateRequest;
import ru.emitrohin.privateclubbackend.dto.request.material.MaterialUpdateRequest;
import ru.emitrohin.privateclubbackend.dto.response.material.AdminMaterialResponse;
import ru.emitrohin.privateclubbackend.dto.response.EnrollmentResponse;
import ru.emitrohin.privateclubbackend.dto.response.material.MaterialResponse;
import ru.emitrohin.privateclubbackend.model.EnrollmentStatus;
import ru.emitrohin.privateclubbackend.model.MaterialEnrollment;
import ru.emitrohin.privateclubbackend.repository.MaterialEnrollmentRepository;
import ru.emitrohin.privateclubbackend.repository.MaterialRepository;
import ru.emitrohin.privateclubbackend.repository.TopicRepository;
import ru.emitrohin.privateclubbackend.repository.UserRepository;
import ru.emitrohin.privateclubbackend.util.UserUtils;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository materialRepository;

    private final TopicRepository topicRepository;

    private final UserRepository userRepository;

    private final MaterialEnrollmentRepository materialEnrollmentRepository;

    private final MaterialMapper materialMapper;

    private final EnrollmentMapper enrollmentMapper;

    public Optional<AdminMaterialResponse> findById(UUID materialId) {
        return materialRepository.findById(materialId).map(materialMapper::toAdminMaterialResponse);
    }

    public Optional<MaterialResponse> findByIdAndPublishedTrue(UUID materialId) {
        return materialRepository.findByIdAndPublishedTrue(materialId).map(materialMapper::toMaterialResponse);
    }

    @Transactional
    public AdminMaterialResponse createMaterial(MaterialCreateRequest createRequest, String coverObjectKey, String mediaObjectKey) {
        var topicId = createRequest.topicId();
        var topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found with id: " + topicId)); //TODO свой тип исключения?
        var newMaterial = materialMapper.fromMaterialCreateRequest(createRequest, topic, coverObjectKey, mediaObjectKey);
        var savedMaterial = materialRepository.save(newMaterial);
        return materialMapper.toAdminMaterialResponse(savedMaterial);
    }

    @Transactional
    public Optional<AdminMaterialResponse> updateMaterial(UUID materialId, MaterialUpdateRequest updateRequest, String coverObjectKey, String mediaObjectKey) {
        var topic = topicRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Topic not found with id: " + materialId));
        return materialRepository.findById(materialId)
                .map(material -> materialRepository.save(materialMapper.fromMaterialUpdateRequest(updateRequest, topic, coverObjectKey, mediaObjectKey)))
                .map(materialMapper::toAdminMaterialResponse);
    }

    public Optional<AdminMaterialResponse> updatePublishedStatus(UUID materialId, boolean published) {
        return materialRepository.findById(materialId)
                .map(topic -> {
                    topic.setPublished(published);
                    return materialRepository.save(topic);
                }).map(materialMapper::toAdminMaterialResponse);
    }

    @Transactional
    public EnrollmentResponse setTopicEnrollmentStatus(UUID materialId, EnrollmentStatus status) {
        var material = materialRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Material not found with id: " + materialId)); //TODO свой тип исключения?

        var userId = UserUtils.getCurrentUserId();
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId)); //TODO свой тип исключения?

        var materialEnrollment = materialEnrollmentRepository.findByMaterial_IdAndUser_Id(materialId, userId)
                .orElseGet(() -> {
                    var enrollment = new MaterialEnrollment();
                    enrollment.setMaterial(material);
                    enrollment.setUser(user);
                    enrollment.setStatus(status);
                    return materialEnrollmentRepository.save(enrollment);
                });

        return enrollmentMapper.fromMaterialEnrollment(materialEnrollment);
    }

    @Transactional
    public void deleteById(UUID id) {
        materialRepository.findById(id).ifPresent(material -> {
            if (!material.getEnrollments().isEmpty()) {
                throw new IllegalStateException("Cannot delete material with active enrollments");
            }

            materialRepository.deleteById(id);
        });
    }
}
