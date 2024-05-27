package ru.emitrohin.privateclubbackend.util;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordUtils {

    private final PasswordEncoder passwordEncoder;

    @Named("encodePassword")
    public String encode(String password) {
        return passwordEncoder.encode(password);
    }
}