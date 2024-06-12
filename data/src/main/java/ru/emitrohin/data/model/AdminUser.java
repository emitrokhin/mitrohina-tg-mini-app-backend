package ru.emitrohin.data.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "admin_users")
@Getter
@Setter
//TODO роль и последний визит можно выделить в родительскую сущность. Можно подключить интерфейс spring security
//TODO добавить свойство бан для всех пользователей
public class AdminUser extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "last_visit", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime lastVisit = LocalDateTime.now();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdminUser user = (AdminUser) o;
        return getId() != null && getId().equals(user.getId());
    }

    @Override
    public String toString() {
        return "AdminUser{" +
                "username='" + username + '\'' +
                ", role=" + role +
                ", lastVisit=" + lastVisit +
                '}';
    }
}
