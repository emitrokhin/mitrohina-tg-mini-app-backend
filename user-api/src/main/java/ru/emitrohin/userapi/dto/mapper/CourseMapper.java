package ru.emitrohin.userapi.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ru.emitrohin.data.model.Course;
import ru.emitrohin.data.model.Topic;
import ru.emitrohin.userapi.dto.response.course.CourseDetailsResponse;
import ru.emitrohin.userapi.dto.response.course.CourseSummaryResponse;
import ru.emitrohin.userapi.services.S3Service;

@Mapper(componentModel = "spring", uses = S3Service.class)
public interface CourseMapper {

    @Mapping(target = "coverUrl", source = "coverObjectKey", qualifiedByName = "generatePermanentUrl")
    CourseDetailsResponse toCourseDetailsResponse(Course course);

    @Mapping(target = "coverUrl", source = "coverObjectKey", qualifiedByName = "generatePermanentUrl")
    CourseDetailsResponse.TopicSummary toTopicSummary(Topic topic);

    @Mapping(target = "coverUrl", source = "coverObjectKey", qualifiedByName = "generatePermanentUrl")
    CourseSummaryResponse toCourseSummaryResponse(Course course);
}