package ru.emitrohin.adminui.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.emitrohin.adminui.dto.request.LoginPasswordRequest;
import ru.emitrohin.adminui.dto.request.PasswordUpdateRequest;
import ru.emitrohin.adminui.dto.response.AdminUserResponse;
import ru.emitrohin.adminui.util.PasswordUtils;
import ru.emitrohin.data.model.AdminUser;


@Mapper(componentModel = "spring", uses = PasswordUtils.class)
public interface AdminUserMapper {

    AdminUserResponse toResponse(AdminUser adminUser);

    @Mapping(target = "role", constant = "ADMIN")
    @Mapping(target = "password", qualifiedByName = "encodePassword")
    AdminUser fromPasswordLoginRequest(LoginPasswordRequest request);

    @Mapping(target = "password", qualifiedByName = "encodePassword")
    void update(@MappingTarget AdminUser adminUser, PasswordUpdateRequest updateRequest);
}