package ru.emitrohin.userapi.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

public class UserUtils {
    public static UUID getCurrentUserId() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .map(Object::toString)
                .map(UUID::fromString)
                .orElseThrow(() -> new RuntimeException("User is not authenticated")); //TODO свой тип исключения?
    }
}
