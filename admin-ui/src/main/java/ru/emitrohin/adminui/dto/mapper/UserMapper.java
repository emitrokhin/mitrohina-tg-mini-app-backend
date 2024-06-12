package ru.emitrohin.adminui.dto.mapper;

import org.mapstruct.Mapper;
import ru.emitrohin.adminui.dto.request.telegram.TelegramUserRequest;
import ru.emitrohin.adminui.dto.response.UserResponse;
import ru.emitrohin.data.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);

    //@Mapping(target = "createdAt", ignore = true)
    User fromUserRequest(TelegramUserRequest telegramUserRequest);
}