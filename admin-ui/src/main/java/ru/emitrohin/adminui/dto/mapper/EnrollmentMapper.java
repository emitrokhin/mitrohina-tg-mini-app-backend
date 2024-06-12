package ru.emitrohin.adminui.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.emitrohin.adminui.dto.response.EnrollmentResponse;
import ru.emitrohin.adminui.services.S3Service;
import ru.emitrohin.data.model.CourseEnrollment;
import ru.emitrohin.data.model.MaterialEnrollment;
import ru.emitrohin.data.model.TopicEnrollment;

@Mapper(componentModel = "spring", uses= S3Service.class)
public interface EnrollmentMapper {

    @Mapping(source = "id", target = "enrollmentId")
    EnrollmentResponse fromCourseEnrollment(CourseEnrollment enrollment);
}