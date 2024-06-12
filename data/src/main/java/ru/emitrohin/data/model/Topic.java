package ru.emitrohin.data.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "topics")
@Getter
@Setter
public class Topic extends PublishableEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(name = "cover_object_key", nullable = false)
    private String coverObjectKey;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<Material> materials;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TopicEnrollment> enrollments;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Topic course = (Topic) o;
        return getId() != null && getId().equals(course.getId());
    }

    @Override
    public String toString() {
        return "Topic{" +
                "id=" + getId() +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", coverObjectKey='" + coverObjectKey + '\'' +
                ", course=" + course.getTitle() +
                '}';
    }
}
