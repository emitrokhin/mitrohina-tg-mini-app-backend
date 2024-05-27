package ru.emitrohin.privateclubbackend.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import ru.emitrohin.privateclubbackend.dto.request.LoginPasswordRequest;
import ru.emitrohin.privateclubbackend.dto.request.PasswordUpdateRequest;
import ru.emitrohin.privateclubbackend.dto.response.AdminUserResponse;
import ru.emitrohin.privateclubbackend.model.AdminUser;
import ru.emitrohin.privateclubbackend.util.PasswordUtils;

@Mapper(componentModel = "spring", uses = PasswordUtils.class)
public interface AdminUserMapper {
    AdminUserMapper INSTANCE = Mappers.getMapper(AdminUserMapper.class);

    AdminUserResponse toResponse(AdminUser adminUser);

    @Mapping(target = "role", constant = "ADMIN")
    @Mapping(target = "password", qualifiedByName = "encodePassword")
    AdminUser fromPasswordLoginRequest(LoginPasswordRequest request);

    @Mapping(target = "password", qualifiedByName = "encodePassword")
    void update(@MappingTarget AdminUser adminUser, PasswordUpdateRequest updateRequest);
}