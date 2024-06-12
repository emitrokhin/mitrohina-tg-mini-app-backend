package ru.emitrohin.userapi.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ru.emitrohin.data.model.Role;
import ru.emitrohin.data.model.User;

import ru.emitrohin.userapi.dto.mapper.UserMapper;
import ru.emitrohin.userapi.dto.request.telegram.TelegramUserRequest;
import ru.emitrohin.userapi.dto.response.UserResponse;
import ru.emitrohin.userapi.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    private final UserMapper userMapper;

    public Optional<UserResponse> findById(UUID id) {
        return repository.findById(id).map(userMapper::toResponse);
    }

    //TODO разобраться. по умолчанию сервис сразу мапит в dto. это правильно? в текущем случае мне нужен четко пользователь
    public Optional<User> findByTelegramId(long telegramId) {
        return repository.findByTelegramId(telegramId);
    }

    public UserResponse save(TelegramUserRequest telegramUserRequest) {
        User newUser = userMapper.fromUserRequest(telegramUserRequest);
        //TODO это должен установить либо гибернейт либо бд
        newUser.setRole(Role.USER);
        newUser.setLastVisit(LocalDateTime.now());
        User savedUser = repository.save(newUser);
        return userMapper.toResponse(savedUser);
    }

    @Transactional
    public Optional<UserResponse> updateUser(UUID id, TelegramUserRequest telegramUserRequest) {
        return repository.findById(id)
                .map(user -> repository.save(userMapper.fromUserRequest(telegramUserRequest)))
                .map(userMapper::toResponse);
    }

    public Optional<UserResponse> getCurrentUser() {
        var id = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
        return findById(id);
    }
}