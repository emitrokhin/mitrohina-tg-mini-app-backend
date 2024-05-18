package ru.emitrohin.privateclubbackend.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.emitrohin.privateclubbackend.dto.response.UserResponse;
import ru.emitrohin.privateclubbackend.service.UserService;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/my")
public class UserController {

    private final UserService userService;

    @GetMapping("/account")
    public ResponseEntity<UserResponse> getCurrentUser() {
        Optional<UserResponse> userDto = userService.getCurrentUser();
        return userDto
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}