package ru.emitrohin.privateclubbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.emitrohin.privateclubbackend.model.AdminUser;

import java.util.Optional;
import java.util.UUID;

public interface AdminUserRepository extends JpaRepository<AdminUser, UUID> {
    Optional<AdminUser> findByUsername(String username);
}