-- Вставка данных в таблицу courses
INSERT INTO courses (id, created_at, updated_at, title, description, cover_object_key, published)
VALUES ('223e4567-e89b-12d3-a456-426614174001', now(), now(), 'Course 1', 'Description for Course 1', 'cover-key-1', true),
       ('223e4567-e89b-12d3-a456-426614174002', now(), now(), 'Course 2', 'Description for Course 2', 'cover-key-2', true),
       ('223e4567-e89b-12d3-a456-426614174003', now(), now(), 'Course 3', 'Description for Course 3', 'cover-key-3', false);

-- Вставка данных в таблицу topics
INSERT INTO topics (id, created_at, updated_at, title, description, cover_object_key, course_id, published)
VALUES ('323e4567-e89b-12d3-a456-426614174001', now(), now(), 'Topic 1-1', 'Description for Topic 1-1', 'cover-key-1-1', '223e4567-e89b-12d3-a456-426614174001', true),
       ('323e4567-e89b-12d3-a456-426614174002', now(), now(), 'Topic 1-2', 'Description for Topic 1-2', 'cover-key-1-2', '223e4567-e89b-12d3-a456-426614174001', true),
       ('323e4567-e89b-12d3-a456-426614174003', now(), now(), 'Topic 1-3', 'Description for Topic 1-3', 'cover-key-1-3', '223e4567-e89b-12d3-a456-426614174001', false),
       ('323e4567-e89b-12d3-a456-426614174004', now(), now(), 'Topic 2-1', 'Description for Topic 2-1', 'cover-key-2-1', '223e4567-e89b-12d3-a456-426614174002', true),
       ('323e4567-e89b-12d3-a456-426614174005', now(), now(), 'Topic 2-2', 'Description for Topic 2-2', 'cover-key-2-2', '223e4567-e89b-12d3-a456-426614174002', false),
       ('323e4567-e89b-12d3-a456-426614174006', now(), now(), 'Topic 2-3', 'Description for Topic 2-3', 'cover-key-2-3', '223e4567-e89b-12d3-a456-426614174002', false),
       ('323e4567-e89b-12d3-a456-426614174007', now(), now(), 'Topic 3-1', 'Description for Topic 3-1', 'cover-key-3-1', '223e4567-e89b-12d3-a456-426614174003', false),
       ('323e4567-e89b-12d3-a456-426614174008', now(), now(), 'Topic 3-2', 'Description for Topic 3-2', 'cover-key-3-2', '223e4567-e89b-12d3-a456-426614174003', false),
       ('323e4567-e89b-12d3-a456-426614174009', now(), now(), 'Topic 3-3', 'Description for Topic 3-3', 'cover-key-3-3','223e4567-e89b-12d3-a456-426614174003', false);

-- Вставка данных в таблицу materials
INSERT INTO materials (id, created_at, updated_at, title, duration, media_object_key, cover_object_key, topic_id, published)
VALUES ('423e4567-e89b-12d3-a456-426614174001', now(), now(), 'Material 1-1-1', 120, 'media-key-1-1-1', 'cover-key-1-1-1', '323e4567-e89b-12d3-a456-426614174001', true),
       ('423e4567-e89b-12d3-a456-426614174002', now(), now(), 'Material 1-1-2', 90, 'media-key-1-1-2', 'cover-key-1-1-2', '323e4567-e89b-12d3-a456-426614174001', false),
       ('423e4567-e89b-12d3-a456-426614174003', now(), now(), 'Material 1-1-3', 60, 'media-key-1-1-3', 'cover-key-1-1-3', '323e4567-e89b-12d3-a456-426614174001', true),
       ('423e4567-e89b-12d3-a456-426614174004', now(), now(), 'Material 1-2-1', 120, 'media-key-1-2-1', 'cover-key-1-2-1', '323e4567-e89b-12d3-a456-426614174002', false),
       ('423e4567-e89b-12d3-a456-426614174005', now(), now(), 'Material 1-2-2', 90, 'media-key-1-2-2',
        'cover-key-1-2-2', '323e4567-e89b-12d3-a456-426614174002', true),
       ('423e4567-e89b-12d3-a456-426614174006', now(), now(), 'Material 1-2-3', 60, 'media-key-1-2-3',
        'cover-key-1-2-3', '323e4567-e89b-12d3-a456-426614174002', true),
       ('423e4567-e89b-12d3-a456-426614174007', now(), now(), 'Material 1-3-1', 120, 'media-key-1-3-1',
        'cover-key-1-3-1', '323e4567-e89b-12d3-a456-426614174003', false),
       ('423e4567-e89b-12d3-a456-426614174008', now(), now(), 'Material 1-3-2', 90, 'media-key-1-3-2',
        'cover-key-1-3-2', '323e4567-e89b-12d3-a456-426614174003', true),
       ('423e4567-e89b-12d3-a456-426614174009', now(), now(), 'Material 1-3-3', 60, 'media-key-1-3-3',
        'cover-key-1-3-3', '323e4567-e89b-12d3-a456-426614174003', true),
       ('423e4567-e89b-12d3-a456-426614174010', now(), now(), 'Material 2-1-1', 120, 'media-key-2-1-1',
        'cover-key-2-1-1', '323e4567-e89b-12d3-a456-426614174004', false),
       ('423e4567-e89b-12d3-a456-426614174011', now(), now(), 'Material 2-1-2', 90, 'media-key-2-1-2',
        'cover-key-2-1-2', '323e4567-e89b-12d3-a456-426614174004', true),
       ('423e4567-e89b-12d3-a456-426614174012', now(), now(), 'Material 2-1-3', 60, 'media-key-2-1-3',
        'cover-key-2-1-3', '323e4567-e89b-12d3-a456-426614174004', false),
       ('423e4567-e89b-12d3-a456-426614174013', now(), now(), 'Material 2-2-1', 120, 'media-key-2-2-1',
        'cover-key-2-2-1', '323e4567-e89b-12d3-a456-426614174005', false),
       ('423e4567-e89b-12d3-a456-426614174014', now(), now(), 'Material 2-2-2', 90, 'media-key-2-2-2',
        'cover-key-2-2-2', '323e4567-e89b-12d3-a456-426614174005', true),
       ('423e4567-e89b-12d3-a456-426614174015', now(), now(), 'Material 2-2-3', 60, 'media-key-2-2-3',
        'cover-key-2-2-3', '323e4567-e89b-12d3-a456-426614174005', true),
       ('423e4567-e89b-12d3-a456-426614174016', now(), now(), 'Material 2-3-1', 120, 'media-key-2-3-1',
        'cover-key-2-3-1', '323e4567-e89b-12d3-a456-426614174006', true),
       ('423e4567-e89b-12d3-a456-426614174017', now(), now(), 'Material 2-3-2', 90, 'media-key-2-3-2',
        'cover-key-2-3-2', '323e4567-e89b-12d3-a456-426614174006', false),
       ('423e4567-e89b-12d3-a456-426614174018', now(), now(), 'Material 2-3-3', 60, 'media-key-2-3-3',
        'cover-key-2-3-3', '323e4567-e89b-12d3-a456-426614174006', false),
       ('423e4567-e89b-12d3-a456-426614174019', now(), now(), 'Material 3-1-1', 120, 'media-key-3-1-1',
        'cover-key-3-1-1', '323e4567-e89b-12d3-a456-426614174007', false),
       ('423e4567-e89b-12d3-a456-426614174020', now(), now(), 'Material 3-1-2', 90, 'media-key-3-1-2',
        'cover-key-3-1-2', '323e4567-e89b-12d3-a456-426614174007', false),
       ('423e4567-e89b-12d3-a456-426614174021', now(), now(), 'Material 3-1-3', 60, 'media-key-3-1-3',
        'cover-key-3-1-3', '323e4567-e89b-12d3-a456-426614174007', true),
       ('423e4567-e89b-12d3-a456-426614174022', now(), now(), 'Material 3-2-1', 120, 'media-key-3-2-1',
        'cover-key-3-2-1', '323e4567-e89b-12d3-a456-426614174008', true),
       ('423e4567-e89b-12d3-a456-426614174023', now(), now(), 'Material 3-2-2', 90, 'media-key-3-2-2',
        'cover-key-3-2-2', '323e4567-e89b-12d3-a456-426614174008', true),
       ('423e4567-e89b-12d3-a456-426614174024', now(), now(), 'Material 3-2-3', 60, 'media-key-3-2-3',
        'cover-key-3-2-3', '323e4567-e89b-12d3-a456-426614174008', false),
       ('423e4567-e89b-12d3-a456-426614174025', now(), now(), 'Material 3-3-1', 120, 'media-key-3-3-1',
        'cover-key-3-3-1', '323e4567-e89b-12d3-a456-426614174009', false),
       ('423e4567-e89b-12d3-a456-426614174026', now(), now(), 'Material 3-3-2', 90, 'media-key-3-3-2',
        'cover-key-3-3-2', '323e4567-e89b-12d3-a456-426614174009', false),
       ('423e4567-e89b-12d3-a456-426614174027', now(), now(), 'Material 3-3-3', 60, 'media-key-3-3-3',
        'cover-key-3-3-3', '323e4567-e89b-12d3-a456-426614174009', false);

-- Вставка данных в таблицу admin_users
INSERT INTO admin_users (id, created_at, updated_at, username, password, role, last_visit) VALUES
    ('523e4567-e89b-12d3-a456-426614174004', now(), now(), 'admin', 'PASSWORD', 'ADMIN', now());

INSERT INTO users (id, created_at, updated_at, telegram_id, first_name, last_name, username, photo_url, role, last_visit) VALUES
                                                                                                                              ('c2fa8f1f-7c30-4b30-ae19-9e5fe83eea39', now(), now(), 1237867890, 'John', null, 'johndoe', null, 'USER', now()),
                                                                                                                              ('065a27d3-c1ef-448b-ae02-b29066d3e650', now(), now(), 9934567891, 'Jane', 'Doe', null, null, 'USER', now()),
                                                                                                                              ('62991587-9363-44a5-bf2f-658471549e43', now(), now(), 3334567892, 'Alice', 'Smith', 'johnappleseed', 'http://example.com/photo3.jpg', 'USER', now());