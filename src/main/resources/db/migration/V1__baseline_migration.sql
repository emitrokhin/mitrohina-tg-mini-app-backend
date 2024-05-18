CREATE TABLE course_enrollments
(
    id         UUID                        NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    user_id    UUID                        NOT NULL,
    topic_id   UUID                        NOT NULL,
    status     VARCHAR(255)                NOT NULL,
    CONSTRAINT pk_course_enrollments PRIMARY KEY (id)
);

CREATE TABLE courses
(
    id          UUID                        NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    title       VARCHAR(255)                NOT NULL,
    description VARCHAR(255)                NOT NULL,
    cover_object_key  VARCHAR(255)          NOT NULL,
    published         BOOLEAN               NOT NULL DEFAULT false,
    CONSTRAINT pk_courses PRIMARY KEY (id)
);

CREATE TABLE material_enrollments
(
    id         UUID                        NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    user_id    UUID                        NOT NULL,
    topic_id   UUID                        NOT NULL,
    status     VARCHAR(255)                NOT NULL,
    CONSTRAINT pk_material_enrollments PRIMARY KEY (id)
);

CREATE TABLE materials
(
    id               UUID                        NOT NULL,
    published        BOOLEAN                     NOT NULL default false,
    created_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    title            VARCHAR(255)                NOT NULL,
    duration         INTEGER                     NOT NULL,
    media_object_key VARCHAR(255)                NOT NULL,
    cover_object_key VARCHAR(255)                NOT NULL,
    topic_id         UUID                        NOT NULL,
    CONSTRAINT pk_materials PRIMARY KEY (id)
);

CREATE TABLE topic_enrollments
(
    id         UUID                        NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    user_id    UUID                        NOT NULL,
    course_id  UUID                        NOT NULL,
    status     VARCHAR(255)                NOT NULL,
    CONSTRAINT pk_topic_enrollments PRIMARY KEY (id)
);

CREATE TABLE topics
(
    id               UUID                        NOT NULL,
    published        BOOLEAN                     NOT NULL DEFAULT false,
    created_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    title            VARCHAR(255)                NOT NULL,
    description      VARCHAR(255)                NOT NULL,
    cover_object_key VARCHAR(255)                NOT NULL,
    course_id        UUID                        NOT NULL,
    CONSTRAINT pk_topics PRIMARY KEY (id)
);

CREATE TABLE users
(
    id          UUID                        NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    telegram_id BIGINT                      NOT NULL,
    first_name  VARCHAR(255)                NOT NULL,
    last_name   VARCHAR(255)                ,
    username    VARCHAR(255)                ,
    photo_url   VARCHAR(255)                ,
    role        VARCHAR(255)                NOT NULL,
    last_visit  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT uc_users_telegram UNIQUE (telegram_id);

ALTER TABLE course_enrollments
    ADD CONSTRAINT FK_COURSE_ENROLLMENTS_ON_TOPIC FOREIGN KEY (topic_id) REFERENCES topics (id);

ALTER TABLE course_enrollments
    ADD CONSTRAINT FK_COURSE_ENROLLMENTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE materials
    ADD CONSTRAINT FK_MATERIALS_ON_TOPIC FOREIGN KEY (topic_id) REFERENCES topics (id);

ALTER TABLE material_enrollments
    ADD CONSTRAINT FK_MATERIAL_ENROLLMENTS_ON_TOPIC FOREIGN KEY (topic_id) REFERENCES materials (id);

ALTER TABLE material_enrollments
    ADD CONSTRAINT FK_MATERIAL_ENROLLMENTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE topics
    ADD CONSTRAINT FK_TOPICS_ON_COURSE FOREIGN KEY (course_id) REFERENCES courses (id);

ALTER TABLE topic_enrollments
    ADD CONSTRAINT FK_TOPIC_ENROLLMENTS_ON_COURSE FOREIGN KEY (course_id) REFERENCES topics (id);

ALTER TABLE topic_enrollments
    ADD CONSTRAINT FK_TOPIC_ENROLLMENTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);