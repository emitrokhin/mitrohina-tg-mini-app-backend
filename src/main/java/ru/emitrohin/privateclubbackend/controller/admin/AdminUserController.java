package ru.emitrohin.privateclubbackend.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.emitrohin.privateclubbackend.dto.request.TelegramUserRequest;
import ru.emitrohin.privateclubbackend.dto.UserResponse;
import ru.emitrohin.privateclubbackend.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/admin/users")
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
}
