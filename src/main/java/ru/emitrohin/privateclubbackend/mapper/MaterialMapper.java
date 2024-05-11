package ru.emitrohin.privateclubbackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.emitrohin.privateclubbackend.dto.MaterialRequest;
import ru.emitrohin.privateclubbackend.dto.MaterialResponse;
import ru.emitrohin.privateclubbackend.model.Material;

@Mapper
public interface MaterialMapper {
    MaterialMapper INSTANCE = Mappers.getMapper(MaterialMapper.class);

    @Mapping(source = "topic.id", target = "topicId")
    MaterialResponse toResponse(Material course);

    @Mapping(source = "topicId", target = "topic.id")
    Material toMaterial(MaterialRequest userDto);
}
