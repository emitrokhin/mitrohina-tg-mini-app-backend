package ru.emitrohin.adminui.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.emitrohin.adminui.dto.request.LoginPasswordRequest;
import ru.emitrohin.adminui.dto.request.PasswordUpdateRequest;
import ru.emitrohin.adminui.dto.response.AdminUserResponse;
import ru.emitrohin.adminui.dto.response.UserResponse;
import ru.emitrohin.adminui.services.UserService;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
//TODO разделить на два контроллера для админов и простых
//TODO переименовать пакет и api c admin на private
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

    //TODO а нужен ли такой endpoint? вручную по умолчанию ничего не редактируется, тем более не через TelegramUserRequest
    /*
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable("id") UUID id,
                                                   @Valid @RequestBody TelegramUserRequest telegramUserRequest) {
        Optional<UserResponse> updatedUser = userService.updateUser(id, telegramUserRequest);
        return updatedUser
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    */

    //TODO ban user

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

    @PutMapping("/admins/{id}/update-password")
    //TODO при смене пароля надо инвалидировать jwt токен
    public ResponseEntity<AdminUserResponse> updateAdminUserPassword(@PathVariable("id") UUID id,
                                                                     @Valid @RequestBody PasswordUpdateRequest updateRequest) {
        return ResponseEntity.ok(userService.updateAdminUser(id, updateRequest));
    }

    //TODO метод полного обновления админа

    @PostMapping("/admins")
    public ResponseEntity<AdminUserResponse> registerAdminUser(@RequestBody @Valid LoginPasswordRequest request) {
        return ResponseEntity.ok(userService.registerAdminUser(request));
    }
}