package ru.emitrohin.privateclubbackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.emitrohin.privateclubbackend.dto.CourseRequest;
import ru.emitrohin.privateclubbackend.dto.CourseResponse;
import ru.emitrohin.privateclubbackend.model.Course;

@Mapper
public interface CourseMapper {
    CourseMapper INSTANCE = Mappers.getMapper(CourseMapper.class);

    CourseResponse toResponse(Course course);

    Course toCourse(CourseRequest request);
}