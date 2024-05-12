package ru.emitrohin.privateclubbackend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.emitrohin.privateclubbackend.dto.MaterialRequest;
import ru.emitrohin.privateclubbackend.dto.response.AdminMaterialResponse;
import ru.emitrohin.privateclubbackend.dto.response.MaterialResponse;
import ru.emitrohin.privateclubbackend.mapper.MaterialMapper;
import ru.emitrohin.privateclubbackend.model.Material;
import ru.emitrohin.privateclubbackend.repository.MaterialRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository materialRepository;

    public Optional<AdminMaterialResponse> adminFindById(UUID dataId) {
        return materialRepository.findById(dataId).map(MaterialMapper.INSTANCE::toAdminResponse);
    }

    //TODO возникает путаница. где мапить то? мне кажется сервисы должгы это делать, но тогда в контроллере непонятно
    //чего ожидать
    public Optional<Material> findById(UUID dataId) {
        return materialRepository.findById(dataId);
    }

    public List<MaterialResponse> findAllByTopicId(UUID topicId) {
        return materialRepository.findByTopicId(topicId)
                .stream()
                .map(MaterialMapper.INSTANCE::toResponse)
                .collect(Collectors.toList());
    }

    public MaterialResponse save(MaterialRequest materialRequest) {
        Material newData = MaterialMapper.INSTANCE.toMaterial(materialRequest);
        Material savedData = materialRepository.save(newData);
        return MaterialMapper.INSTANCE.toResponse(savedData);
    }

    @Transactional
    public Optional<MaterialResponse> update(UUID id, MaterialRequest materialRequest) {
        return materialRepository.findById(id)
                .map(data -> materialRepository.save(MaterialMapper.INSTANCE.toMaterial(materialRequest)))
                .map(MaterialMapper.INSTANCE::toResponse);
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
