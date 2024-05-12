package ru.emitrohin.privateclubbackend.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import ru.emitrohin.privateclubbackend.dto.MaterialRequest;
import ru.emitrohin.privateclubbackend.dto.response.AdminMaterialResponse;
import ru.emitrohin.privateclubbackend.dto.response.MaterialResponse;
import ru.emitrohin.privateclubbackend.model.Material;

@Mapper
public interface MaterialMapper {
    MaterialMapper INSTANCE = Mappers.getMapper(MaterialMapper.class);

    @Mapping(source = "topic.id", target = "topicId")
    MaterialResponse toResponse(Material material, @Context String mediaUrl);

    @Mapping(source = "topic.id", target = "topicId")
    MaterialResponse toResponse(Material material);

    @Mapping(source = "topic.id", target = "topicId")
    AdminMaterialResponse toAdminResponse(Material material);

    @Mapping(source = "topicId", target = "topic.id")
    Material toMaterial(MaterialRequest userDto);
}
