package ru.emitrohin.privateclubbackend.dto.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.emitrohin.privateclubbackend.dto.request.topic.TopicCreateRequest;
import ru.emitrohin.privateclubbackend.dto.request.topic.TopicUpdateRequest;
import ru.emitrohin.privateclubbackend.dto.response.topic.AdminTopicDetailsResponse;
import ru.emitrohin.privateclubbackend.dto.response.topic.AdminTopicSummaryResponse;
import ru.emitrohin.privateclubbackend.dto.response.topic.TopicDetailsResponse;
import ru.emitrohin.privateclubbackend.model.Course;
import ru.emitrohin.privateclubbackend.model.Topic;
import ru.emitrohin.privateclubbackend.util.S3Utils;

@Mapper(componentModel = "spring", uses = S3Utils.class)
public interface TopicMapper {

    Topic fromTopicCreateRequest(TopicCreateRequest topicCreateRequest, @Context Course course, @Context String objectKey);

    Topic fromTopicUpdateRequest(TopicUpdateRequest topicCreateRequest, @Context Course course, @Context String objectKey);

    @Mapping(target = "coverUrl", source = "coverObjectKey", qualifiedByName = "generatePermanentUrl")
    @Mapping(source = "course.id", target = "courseId")
    TopicDetailsResponse toTopicDetailsResponse(Topic topic);

    @Mapping(target = "coverUrl", source = "coverObjectKey", qualifiedByName = "generatePermanentUrl")
    @Mapping(source = "course.id", target = "courseId")
    AdminTopicDetailsResponse toAdminTopicDetailsResponse(Topic topic);

    @Mapping(target = "coverUrl", source = "coverObjectKey", qualifiedByName = "generatePermanentUrl")
    @Mapping(source = "course.id", target = "courseId")
    AdminTopicSummaryResponse toAdminTopicSummaryResponse(Topic topic);
}
