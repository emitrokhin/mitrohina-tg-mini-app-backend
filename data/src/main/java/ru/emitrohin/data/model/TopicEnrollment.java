package ru.emitrohin.data.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "topic_enrollments")
public class TopicEnrollment extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Topic topic;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EnrollmentStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopicEnrollment topicEnrollment = (TopicEnrollment) o;
        return getId() != null && getId().equals(topicEnrollment.getId());
    }

    @Override
    public String toString() {
        return "TopicEnrollment{" +
                "user=" + user.getId() +
                ", topic=" + topic.getId() +
                ", status=" + status.name() +
                '}';
    }
}