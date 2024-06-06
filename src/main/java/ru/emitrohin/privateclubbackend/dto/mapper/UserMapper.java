package ru.emitrohin.privateclubbackend.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.emitrohin.privateclubbackend.dto.request.telegram.TelegramUserRequest;
import ru.emitrohin.privateclubbackend.dto.response.UserResponse;
import ru.emitrohin.privateclubbackend.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);

    //@Mapping(target = "createdAt", ignore = true)
    User fromUserRequest(TelegramUserRequest telegramUserRequest);
}