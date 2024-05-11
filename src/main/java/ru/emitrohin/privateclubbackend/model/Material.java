package ru.emitrohin.privateclubbackend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "materials")
@Getter
@Setter
public class Material extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String mediaUrl;

    @ManyToOne
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @OneToMany(mappedBy = "material", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MaterialEnrollment> enrollments;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Material material = (Material) o;
        return getId() != null && getId().equals(material.getId());
    }

    @Override
    public String toString() {
        return "Material{" +
                "id=" + getId() +
                ", title='" + title + '\'' +
                ", mediaUrl='" + mediaUrl + '\'' +
                '}';
    }
}