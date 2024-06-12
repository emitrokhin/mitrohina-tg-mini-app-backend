package ru.emitrohin.adminui.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.emitrohin.adminui.dto.request.LoginPasswordRequest;
import ru.emitrohin.adminui.dto.response.JwtAuthenticationResponse;
import ru.emitrohin.data.model.AdminUser;
import ru.emitrohin.data.model.Role;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginPasswordAuthenticationService {

    private final JwtService jwtService;

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    public JwtAuthenticationResponse authenticateAdmin(LoginPasswordRequest loginRequest) {
        Optional<AdminUser> adminUser = userService.findAdminByUsername(loginRequest.username());
        if (adminUser.isPresent() && passwordEncoder.matches(loginRequest.password(), adminUser.get().getPassword())) {
            if (adminUser.get().getRole() == Role.ADMIN) {
                String token = jwtService.generateTokenForUUID(adminUser.get().getId());
                return new JwtAuthenticationResponse(token);
            } else {
                throw new RuntimeException("User is not an admin"); //TODO свой тип исключения
            }
        } else {
            throw new BadCredentialsException("Invalid username or password");
        }
    }
}
