package ru.emitrohin.privateclubbackend.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.emitrohin.privateclubbackend.dto.UserResponse;
import ru.emitrohin.privateclubbackend.service.UserService;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/my")
public class UserController {

    private final UserService userService;

    @GetMapping("/account")
    public ResponseEntity<UserResponse> getCurrentUser() {
        Optional<UserResponse> userDto = userService.getCurrentUser();
        return userDto
                .map(ResponseEntity::ok)
                //TODO а это правильно, если не найден, такое возвращать? контроллер уже защищен фильтром
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}