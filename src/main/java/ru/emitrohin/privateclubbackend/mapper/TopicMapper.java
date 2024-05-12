package ru.emitrohin.privateclubbackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.emitrohin.privateclubbackend.dto.TopicRequest;
import ru.emitrohin.privateclubbackend.dto.TopicResponse;
import ru.emitrohin.privateclubbackend.model.Topic;

@Mapper
public interface TopicMapper {
    TopicMapper INSTANCE = Mappers.getMapper(TopicMapper.class);

    @Mapping(source = "course.id", target = "courseId")
    TopicResponse toResponse(Topic topic);

    @Mapping(source = "courseId", target = "course.id")
    Topic toTopic(TopicRequest topicRequest);
}
