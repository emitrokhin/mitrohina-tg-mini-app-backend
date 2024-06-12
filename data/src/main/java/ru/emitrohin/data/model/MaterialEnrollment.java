package ru.emitrohin.data.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "material_enrollments")
public class MaterialEnrollment extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "topic_id", nullable = false)
    private Material material;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EnrollmentStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MaterialEnrollment materialEnrollment = (MaterialEnrollment) o;
        return getId() != null && getId().equals(materialEnrollment.getId());
    }

    @Override
    public String toString() {
        return "MaterialEnrollment{" +
                "user=" + user.getId() +
                ", topic=" + material.getId() +
                ", status=" + status.name() +
                '}';
    }
}