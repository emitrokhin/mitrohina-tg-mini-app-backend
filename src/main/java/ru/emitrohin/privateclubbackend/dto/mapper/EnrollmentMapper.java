package ru.emitrohin.privateclubbackend.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.emitrohin.privateclubbackend.dto.response.EnrollmentResponse;
import ru.emitrohin.privateclubbackend.model.CourseEnrollment;
import ru.emitrohin.privateclubbackend.model.MaterialEnrollment;
import ru.emitrohin.privateclubbackend.model.TopicEnrollment;
import ru.emitrohin.privateclubbackend.util.S3Utils;

@Mapper(componentModel = "spring", uses= S3Utils.class)
public interface EnrollmentMapper {

    @Mapping(source = "id", target = "enrollmentId")
    EnrollmentResponse fromCourseEnrollment(CourseEnrollment enrollment);

    @Mapping(source = "id", target = "enrollmentId")
    EnrollmentResponse fromTopicEnrollment(TopicEnrollment enrollment);

    @Mapping(source = "id", target = "enrollmentId")
    EnrollmentResponse fromMaterialEnrollment(MaterialEnrollment enrollment);
}