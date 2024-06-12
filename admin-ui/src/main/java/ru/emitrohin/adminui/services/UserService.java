package ru.emitrohin.adminui.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.emitrohin.adminui.dto.mapper.AdminUserMapper;
import ru.emitrohin.adminui.dto.mapper.UserMapper;
import ru.emitrohin.adminui.dto.request.LoginPasswordRequest;
import ru.emitrohin.adminui.dto.request.PasswordUpdateRequest;
import ru.emitrohin.adminui.dto.request.telegram.TelegramUserRequest;
import ru.emitrohin.adminui.dto.response.AdminUserResponse;
import ru.emitrohin.adminui.dto.response.UserResponse;
import ru.emitrohin.adminui.repository.AdminUserRepository;
import ru.emitrohin.adminui.repository.UserRepository;
import ru.emitrohin.data.model.AdminUser;
import ru.emitrohin.data.model.Role;
import ru.emitrohin.data.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    private final AdminUserRepository adminRepository;
    
    private final UserMapper userMapper;

    private final AdminUserMapper adminUserMapper;

    public Optional<UserResponse> findById(UUID id) {
        return repository.findById(id).map(userMapper::toResponse);
    }

    //TODO разобраться. по умолчанию сервис сразу мапит в dto. это правильно? в текущем случае мне нужен четко пользователь
    public Optional<User> findByTelegramId(long telegramId) {
        return repository.findByTelegramId(telegramId);
    }

    public List<UserResponse> findByFirstName(String firstName) {
        return repository.findByFirstNameContaining(firstName)
                .stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> findByLastName(String firstName) {
        return repository.findByLastNameContaining(firstName)
                .stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> findByUsername(String userName) {
        return repository.findByUsernameContaining(userName)
                .stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
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

    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    public Optional<UserResponse> getCurrentUser() {
        var id = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
        return findById(id);
    }

    public Optional<AdminUser> findAdminByUsername(String username) {
        return adminRepository.findByUsername(username);
    }

    public Optional<AdminUserResponse> findAdminById(UUID adminUserId) {
        return adminRepository.findById(adminUserId).map(adminUserMapper::toResponse);
    }

    //TODO разобраться с названиями и действиями, потому как в норме тут все мапится
    public Optional<AdminUser> findAdminByIdForFilter(UUID adminUserId) {
        return adminRepository.findById(adminUserId);
    }

    public List<AdminUserResponse> findAllAdminUsers() {
        return adminRepository.findAll()
                .stream()
                .map(adminUserMapper::toResponse)
                .collect(Collectors.toList());
    }

    public void deleteAdminUserById(UUID id) {
        adminRepository.deleteById(id);
    }

    public AdminUserResponse registerAdminUser(LoginPasswordRequest request) {
        if (adminRepository.findByUsername(request.username()).isPresent()) {
            throw new IllegalArgumentException("Username is already taken");
        }
        var adminUser = adminUserMapper.fromPasswordLoginRequest(request);
        var savedUser = adminRepository.save(adminUser);
        return adminUserMapper.toResponse(savedUser);
    }

    public AdminUserResponse updateAdminUser(UUID id, PasswordUpdateRequest request) {
        var adminUser = adminRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));//TODO свой тип исключения
        adminUserMapper.update(adminUser, request);
        var updatedUser =  adminRepository.save(adminUser);
        return adminUserMapper.toResponse(updatedUser);
    }
}