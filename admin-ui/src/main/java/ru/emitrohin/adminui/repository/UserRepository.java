package ru.emitrohin.adminui.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.emitrohin.data.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByTelegramId(long telegramId);

    List<User> findByFirstNameContaining(String firstName);

    List<User> findByLastNameContaining(String lastName);

    List<User> findByUsernameContaining(String username);
}