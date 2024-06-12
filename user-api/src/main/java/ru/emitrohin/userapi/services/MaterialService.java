package ru.emitrohin.userapi.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.emitrohin.data.model.EnrollmentStatus;
import ru.emitrohin.data.model.Material;
import ru.emitrohin.data.model.MaterialEnrollment;
import ru.emitrohin.userapi.dto.response.EnrollmentResponse;
import ru.emitrohin.userapi.dto.response.material.MaterialResponse;
import ru.emitrohin.userapi.repository.MaterialEnrollmentRepository;
import ru.emitrohin.userapi.repository.MaterialRepository;
import ru.emitrohin.userapi.repository.TopicRepository;
import ru.emitrohin.userapi.repository.UserRepository;
import ru.emitrohin.userapi.util.UserUtils;
import ru.emitrohin.userapi.dto.mapper.EnrollmentMapper;
import ru.emitrohin.userapi.dto.mapper.MaterialMapper;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository materialRepository;

    private final UserRepository userRepository;

    private final MaterialEnrollmentRepository materialEnrollmentRepository;

    private final MaterialMapper materialMapper;

    private final EnrollmentMapper enrollmentMapper;

    public Optional<MaterialResponse> findByIdAndPublishedTrue(UUID materialId) {
        return materialRepository.findByIdAndPublishedTrue(materialId).map(materialMapper::toMaterialResponse);
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

    public Optional<String> getMaterialObjectKey(UUID materialId) {
        return materialRepository.findById(materialId).map(Material::getCoverObjectKey);
    }
}
