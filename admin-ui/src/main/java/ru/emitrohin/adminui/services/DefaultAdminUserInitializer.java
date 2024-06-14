package ru.emitrohin.adminui.services;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.emitrohin.adminui.repository.AdminUserRepository;
import ru.emitrohin.data.model.AdminUser;
import ru.emitrohin.data.model.Role;

@Service
@RequiredArgsConstructor
//TODO задокументировать
public class DefaultAdminUserInitializer implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(DefaultAdminUserInitializer.class);

    private final AdminUserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            var admin = new AdminUser();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("password"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
            logger.info("Default admin user created. Use admin/password to login. Don't forget to change password.");
        }
    }
}