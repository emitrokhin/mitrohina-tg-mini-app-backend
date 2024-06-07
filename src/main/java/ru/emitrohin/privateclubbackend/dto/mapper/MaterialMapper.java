package ru.emitrohin.privateclubbackend.dto.mapper;

import org.mapstruct.*;
import ru.emitrohin.privateclubbackend.dto.request.material.MaterialCreateRequest;
import ru.emitrohin.privateclubbackend.dto.request.material.MaterialUpdateRequest;
import ru.emitrohin.privateclubbackend.dto.response.material.AdminMaterialResponse;
import ru.emitrohin.privateclubbackend.dto.response.material.MaterialResponse;
import ru.emitrohin.privateclubbackend.model.Material;
import ru.emitrohin.privateclubbackend.model.Topic;
import ru.emitrohin.privateclubbackend.service.S3Service;

@Mapper(componentModel = "spring", uses = S3Service.class)
public interface MaterialMapper {

    Material fromMaterialCreateRequest(MaterialCreateRequest createRequest,
                                       @Context Topic topic, String coverObjectKey, String mediaObjectKey);

    Material fromMaterialUpdateRequest(MaterialUpdateRequest createRequest,
                                       @Context Topic topic, String coverObjectKey, String mediaObjectKey);

    @Mapping(source = "topic.id", target = "topicId")
    @Mapping(target = "coverUrl", source = "coverObjectKey", qualifiedByName = "generatePermanentUrl")
    @Mapping(target = "mediaUrl", source = "mediaObjectKey", qualifiedByName = "generatePresignedUrl")
    MaterialResponse toMaterialResponse(Material material);

    @Mapping(source = "topic.id", target = "topicId")
    @Mapping(target = "coverUrl", source = "coverObjectKey", qualifiedByName = "generatePermanentUrl")
    @Mapping(target = "mediaUrl", source = "mediaObjectKey", qualifiedByName = "generatePresignedUrl")
    AdminMaterialResponse toAdminMaterialResponse(Material material);
}
