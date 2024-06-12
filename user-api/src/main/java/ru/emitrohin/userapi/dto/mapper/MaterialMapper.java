package ru.emitrohin.userapi.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ru.emitrohin.data.model.Material;
import ru.emitrohin.userapi.dto.response.material.MaterialResponse;
import ru.emitrohin.userapi.services.S3Service;

@Mapper(componentModel = "spring", uses = S3Service.class)
public interface MaterialMapper {

    @Mapping(source = "topic.id", target = "topicId")
    @Mapping(target = "coverUrl", source = "coverObjectKey", qualifiedByName = "generatePermanentUrl")
    @Mapping(target = "mediaUrl", source = "mediaObjectKey", qualifiedByName = "generatePresignedUrl")
    MaterialResponse toMaterialResponse(Material material);
}
