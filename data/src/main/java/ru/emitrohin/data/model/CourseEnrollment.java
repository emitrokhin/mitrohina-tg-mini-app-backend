package ru.emitrohin.data.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "course_enrollments")
public class CourseEnrollment extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "topic_id", nullable = false)
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EnrollmentStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseEnrollment courseEnrollment = (CourseEnrollment) o;
        return getId() != null && getId().equals(courseEnrollment.getId());
    }

    @Override
    public String toString() {
        return "CourseEnrollment{" +
                "user=" + user.getId() +
                ", course=" + course.getId() +
                ", status=" + status.name() +
                '}';
    }
}