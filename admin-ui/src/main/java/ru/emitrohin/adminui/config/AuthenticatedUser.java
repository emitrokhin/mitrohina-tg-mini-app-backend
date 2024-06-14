package ru.emitrohin.adminui.config;

import com.vaadin.flow.spring.security.AuthenticationContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.emitrohin.adminui.repository.AdminUserRepository;
import ru.emitrohin.data.model.AdminUser;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthenticatedUser {

    private final AdminUserRepository adminUserRepository;

    private final AuthenticationContext authenticationContext;

    @Transactional
    public Optional<AdminUser> get() {
        return authenticationContext.getAuthenticatedUser(UserDetails.class)
                .flatMap(userDetails -> adminUserRepository.findByUsername(userDetails.getUsername()));
    }

    public void logout() {
        authenticationContext.logout();
    }

}
