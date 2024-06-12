package ru.emitrohin.userapi.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.emitrohin.data.model.Topic;
import ru.emitrohin.userapi.dto.response.topic.TopicDetailsResponse;
import ru.emitrohin.userapi.services.S3Service;

@Mapper(componentModel = "spring", uses = S3Service.class)
public interface TopicMapper {

    @Mapping(target = "coverUrl", source = "coverObjectKey", qualifiedByName = "generatePermanentUrl")
    @Mapping(source = "course.id", target = "courseId")
    TopicDetailsResponse toTopicDetailsResponse(Topic topic);
}
