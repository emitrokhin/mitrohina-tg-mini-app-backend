package ru.emitrohin.privateclubbackend.dto.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.emitrohin.privateclubbackend.dto.request.course.CreateCourseRequest;
import ru.emitrohin.privateclubbackend.dto.request.course.UpdateCourseRequest;
import ru.emitrohin.privateclubbackend.dto.response.course.AdminCourseDetailsResponse;
import ru.emitrohin.privateclubbackend.dto.response.course.AdminCourseSummaryResponse;
import ru.emitrohin.privateclubbackend.dto.response.course.CourseDetailsResponse;
import ru.emitrohin.privateclubbackend.dto.response.course.CourseSummaryResponse;
import ru.emitrohin.privateclubbackend.model.Course;
import ru.emitrohin.privateclubbackend.model.Topic;
import ru.emitrohin.privateclubbackend.service.S3Service;

import java.util.UUID;

@Mapper(componentModel = "spring", uses = S3Service.class)
public interface CourseMapper {

    @Mapping(target = "coverUrl", source = "coverObjectKey", qualifiedByName = "generatePermanentUrl")
    CourseDetailsResponse toCourseDetailsResponse(Course course);

    @Mapping(target = "coverUrl", source = "coverObjectKey", qualifiedByName = "generatePermanentUrl")
    CourseDetailsResponse.TopicSummary toTopicSummary(Topic topic);

    @Mapping(target = "coverUrl", source = "coverObjectKey", qualifiedByName = "generatePermanentUrl")
    CourseSummaryResponse toCourseSummaryResponse(Course course);

    @Mapping(target = "coverUrl", source = "coverObjectKey", qualifiedByName = "generatePermanentUrl")
    AdminCourseSummaryResponse toAdminCourseSummaryResponse(Course course);

    @Mapping(target = "coverUrl", source = "coverObjectKey", qualifiedByName = "generatePermanentUrl")
    AdminCourseDetailsResponse toAdminCourseDetailsResponse(Course course);

    Course toCourse(CreateCourseRequest request, @Context String coverObjectKey);

    Course toCourseWithIdAndObjectKey(UpdateCourseRequest request, @Context UUID id, @Context String coverObjectKey);

}