package ru.emitrohin.adminui.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.emitrohin.adminui.dto.mapper.MaterialMapper;
import ru.emitrohin.adminui.dto.request.material.MaterialCreateRequest;
import ru.emitrohin.adminui.dto.request.material.MaterialUpdateRequest;
import ru.emitrohin.adminui.dto.response.material.AdminMaterialResponse;
import ru.emitrohin.adminui.repository.MaterialRepository;
import ru.emitrohin.adminui.repository.TopicRepository;
import ru.emitrohin.data.model.Material;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository materialRepository;

    private final TopicRepository topicRepository;

    private final MaterialMapper materialMapper;

    public Optional<AdminMaterialResponse> findById(UUID materialId) {
        return materialRepository.findById(materialId).map(materialMapper::toAdminMaterialResponse);
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
    public void deleteById(UUID id) {
        materialRepository.findById(id).ifPresent(material -> {
            if (!material.getEnrollments().isEmpty()) {
                throw new IllegalStateException("Cannot delete material with active enrollments");
            }

            materialRepository.deleteById(id);
        });
    }

    public Optional<String> getMaterialObjectKey(UUID materialId) {
        return materialRepository.findById(materialId).map(Material::getCoverObjectKey);
    }
}
