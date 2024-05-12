package ru.emitrohin.privateclubbackend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends BaseEntity {

    @Column(name = "telegram_id", unique = true, nullable = false)
    private long telegramId;

    @Column(name = "first_name", nullable = false)
    //TODO В телеге могут поменять имя пользователя. Создать отдельную таблицу для измененных данных
    private String firstName;

    //TODO default empty string
    @Column(name = "last_name")
    private String lastName;

    //TODO default empty string
    @Column
    private String username;

    //TODO default empty string
    @Column(name = "photo_url")
    private String photoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "last_visit", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime lastVisit;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return getId() != null && getId().equals(user.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    //TODO что маскируем
    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", telegramId=" + telegramId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                '}';
    }

    /**
     * Маскирует персональные данные для обеспечения конфиденциальности в логах или при выводе.
     * Отображает только первый символ, а остальные символы заменяет на звёздочки.
     */
    private String maskPersonalDataSting(String username) {
        if (username != null && username.length() > 1) {
            return username.charAt(0) + "***";
        }
        return "null";
    }
}
