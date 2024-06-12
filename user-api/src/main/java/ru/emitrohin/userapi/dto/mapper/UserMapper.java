package ru.emitrohin.userapi.dto.mapper;

import org.mapstruct.Mapper;
import ru.emitrohin.data.model.User;
import ru.emitrohin.userapi.dto.request.telegram.TelegramUserRequest;
import ru.emitrohin.userapi.dto.response.UserResponse;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);

    //@Mapping(target = "createdAt", ignore = true)
    User fromUserRequest(TelegramUserRequest telegramUserRequest);
}