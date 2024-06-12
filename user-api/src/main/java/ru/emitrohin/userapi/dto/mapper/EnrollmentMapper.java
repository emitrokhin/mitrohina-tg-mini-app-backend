package ru.emitrohin.userapi.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.emitrohin.data.model.CourseEnrollment;
import ru.emitrohin.data.model.MaterialEnrollment;
import ru.emitrohin.data.model.TopicEnrollment;
import ru.emitrohin.userapi.dto.response.EnrollmentResponse;
import ru.emitrohin.userapi.services.S3Service;

@Mapper(componentModel = "spring", uses= S3Service.class)
public interface EnrollmentMapper {

    @Mapping(source = "id", target = "enrollmentId")
    EnrollmentResponse fromCourseEnrollment(CourseEnrollment enrollment);

    @Mapping(source = "id", target = "enrollmentId")
    EnrollmentResponse fromTopicEnrollment(TopicEnrollment enrollment);

    @Mapping(source = "id", target = "enrollmentId")
    EnrollmentResponse fromMaterialEnrollment(MaterialEnrollment enrollment);
}