package ru.emitrohin.data.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public class PublishableEntity extends BaseEntity {

    private boolean published = false;
}
