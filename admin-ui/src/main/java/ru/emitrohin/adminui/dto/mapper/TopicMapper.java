package ru.emitrohin.adminui.dto.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.emitrohin.adminui.dto.request.topic.TopicCreateRequest;
import ru.emitrohin.adminui.dto.request.topic.TopicUpdateRequest;
import ru.emitrohin.adminui.dto.response.topic.AdminTopicDetailsResponse;
import ru.emitrohin.adminui.dto.response.topic.AdminTopicSummaryResponse;
import ru.emitrohin.adminui.dto.response.topic.TopicDetailsResponse;
import ru.emitrohin.adminui.services.S3Service;
import ru.emitrohin.data.model.Course;
import ru.emitrohin.data.model.Topic;

@Mapper(componentModel = "spring", uses = S3Service.class)
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
