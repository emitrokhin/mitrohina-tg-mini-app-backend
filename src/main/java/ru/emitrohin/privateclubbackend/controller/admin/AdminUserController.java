package ru.emitrohin.privateclubbackend.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.emitrohin.privateclubbackend.dto.request.LoginPasswordRequest;
import ru.emitrohin.privateclubbackend.dto.request.PasswordUpdateRequest;
import ru.emitrohin.privateclubbackend.dto.request.telegram.TelegramUserRequest;
import ru.emitrohin.privateclubbackend.dto.response.AdminUserResponse;
import ru.emitrohin.privateclubbackend.dto.response.UserResponse;
import ru.emitrohin.privateclubbackend.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
//TODO update admin user (password), delete
//TODO разделить на два контроллера для админов и простых
public class AdminUserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable("id") UUID id) {
        //TODO Логировать событие поиска
        Optional<UserResponse> userDto = userService.findById(id);
        return userDto
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping()
    //TODO Установить пагинацию
    //TODO Установить максимум
    //TODO Обратный хронологический порядок
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    //TODO а нужен ли такой endpoint? вручную по умолчанию ничего не создается
    /*@PostMapping()
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody TelegramUserRequest telegramUserRequest) {
        UserResponse newUser = userService.save(telegramUserRequest);
        //TODO uri еще нужно возвращать
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }*/

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable("id") UUID id,
                                                   @Valid @RequestBody TelegramUserRequest telegramUserRequest) {
        Optional<UserResponse> updatedUser = userService.updateUser(id, telegramUserRequest);
        return updatedUser
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable("id") UUID id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build(); //No content is RESTful
    }

    //
    //ADMIN USERS
    //

    @GetMapping("/admins")
    //TODO Установить пагинацию
    //TODO Установить максимум
    //TODO Обратный хронологический порядок
    public ResponseEntity<List<AdminUserResponse>> getAllAdminUsers() {
        List<AdminUserResponse> users = userService.findAllAdminUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/admins/{id}")
    //TODO Установить пагинацию
    //TODO Установить максимум
    //TODO Обратный хронологический порядок
    public ResponseEntity<AdminUserResponse> getAdminUser(@PathVariable("id") UUID id) {
        //TODO Логировать событие поиска
        Optional<AdminUserResponse> userDto = userService.findAdminById(id);
        return userDto
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/admins/{id}")
    public ResponseEntity<AdminUserResponse> deleteAdminUser(@PathVariable("id") UUID id) {
        userService.deleteAdminUserById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/admins/{id}")
    public ResponseEntity<AdminUserResponse> updateAdminUser(@PathVariable("id") UUID id,
                                                             @Valid @RequestBody PasswordUpdateRequest updateRequest) {
        return ResponseEntity.ok(userService.updateAdminUser(id, updateRequest));
    }

    @PostMapping("/admins")
    public ResponseEntity<AdminUserResponse> registerAdminUser(@RequestBody @Valid LoginPasswordRequest request) {
        return ResponseEntity.ok(userService.registerAdminUser(request));
    }
}