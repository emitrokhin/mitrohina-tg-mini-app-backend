package ru.emitrohin.privateclubbackend.integration.process;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.RequestEntity;
import ru.emitrohin.privateclubbackend.dto.response.JwtAuthenticationResponse;
import ru.emitrohin.privateclubbackend.dto.response.UserResponse;
import ru.emitrohin.privateclubbackend.dto.response.course.CourseDetailsResponse;
import ru.emitrohin.privateclubbackend.dto.response.course.CourseSummaryResponse;
import ru.emitrohin.privateclubbackend.dto.response.material.MaterialResponse;
import ru.emitrohin.privateclubbackend.dto.response.topic.TopicDetailsResponse;
import ru.emitrohin.privateclubbackend.integration.AbstractS3Test;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class TelegramUserBasicIntegrationTest extends AbstractS3Test {

    private final static String VALID_JWT_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1MDAwNTY4NDU0IiwiaWF0IjoxNzE3MDAxMDEwLCJleHAiOjE3MTcwMDQ2MTB9.FUAANlm2cIXId-m-9ORlxfFRIu0WcGUVGhSsKlpOack";
    private final static String TELEGRAM_INIT_DATA = "{\"initData\" : \"query_id=AAGGng4qAgAAAIaeDir3Bhb7&user=%7B%22id%22%3A5000568454%2C%22first_name%22%3A%22Evgenii%22%2C%22last_name%22%3A%22%22%2C%22language_code%22%3A%22ru%22%2C%22added_to_attachment_menu%22%3Atrue%2C%22allows_write_to_pm%22%3Atrue%2C%22photo_url%22%3A%22https%3A%5C%2F%5C%2Fa-ttgme.stel.com%5C%2Fi%5C%2Fuserpic%5C%2F320%5C%2FVkTTUx53J9rIIztcvxv37X3IYETqPzwbxE49mcvMnkTG2dQCLy1UDYt3vICAk-B0.svg%22%7D&auth_date=1717001010&hash=4bf601b2e33fe54584bff65f1c343103b8d10b2b6d8fcaf6055b200346c83d34\"}";

    @TestFactory
    //TODO передвинуть в security тесты, s3 зря запускается
    Stream<DynamicTest> tryBrowseUnauthenticatedUser() {
        var getEndpoints = List.of(
                "/my/materials/",
                "/my/courses",
                "/my/courses/completed",
                "/my/courses/enrolled",
                "/my/courses/223e4567-e89b-12d3-a456-426614174002",
                "/my/account",
                "/my/topics/323e4567-e89b-12d3-a456-426614174004"
        );
        var postEndpoints = List.of(
                "/my/materials/423e4567-e89b-12d3-a456-426614174010/complete",
                "/my/materials/423e4567-e89b-12d3-a456-426614174010/enroll",
                "/my/courses/223e4567-e89b-12d3-a456-426614174002/enroll",
                "/my/topics/323e4567-e89b-12d3-a456-426614174004/enroll"
        );

        var getRequests = getEndpoints.stream()
                .map(e -> RequestEntity.get(e).header("Content-Type", "application/json").build());
        var postRequests = postEndpoints.stream()
                .map(e -> RequestEntity.post(e).header("Content-Type", "application/json").build());

        return Stream.concat(getRequests, postRequests)
                .map(requestEntity -> dynamicTest("403: " + requestEntity.toString(), () -> {
                    var response = restTemplate.exchange(requestEntity, Void.class);
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(403)); //unauthorized
                }));
    }

    @TestFactory
    Collection<DynamicTest> browsePublishedCoursesFlow() {
        final UUID courseId = UUID.fromString("223e4567-e89b-12d3-a456-426614174002");
        final UUID topicId = UUID.fromString("323e4567-e89b-12d3-a456-426614174004");
        final UUID materialId = UUID.fromString("423e4567-e89b-12d3-a456-426614174010");

        final AtomicReference<String> jwtToken = new AtomicReference<>();

        return List.of(
                dynamicTest("Получаем initData от телеграм и аутентифицируем", () -> {
                    final var createResponse = restTemplate.exchange(RequestEntity
                                    .post("/api/auth/telegram")
                                    .header("Content-Type", "application/json") //TODO а мои контроллеры вкурсе этого заголовка?
                                    .body(TELEGRAM_INIT_DATA),
                                     JwtAuthenticationResponse.class);

                    assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200)); //ok
                    assertThat(createResponse.getBody()).isNotNull();
                    assertThat(createResponse.getBody()).hasFieldOrPropertyWithValue("token", VALID_JWT_TOKEN);

                    jwtToken.set(createResponse.getBody().token());
                }),
                dynamicTest("Получаем данные о себе", () -> {
                    assertThat(jwtToken.get()).as("Токен не был создан. Пользователь не аутентифицирован").isNotNull();

                    var readPersonalInfo = restTemplate.exchange(RequestEntity
                                    .get("/my/account")
                                    .header("Authorization", "Bearer " + jwtToken)
                                    .build(),
                                     UserResponse.class);

                    assertThat(readPersonalInfo.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200)); //ok
                    assertThat(readPersonalInfo.getBody()).isNotNull();
                    assertThat(readPersonalInfo.getBody()).hasFieldOrProperty("id");
                    assertThat(readPersonalInfo.getBody()).hasFieldOrPropertyWithValue("telegramId", 5000568454L);
                    assertThat(readPersonalInfo.getBody()).hasFieldOrPropertyWithValue("firstName", "Evgenii");
                    assertThat(readPersonalInfo.getBody()).hasFieldOrPropertyWithValue("lastName", null); //TODO разобраться null
                    assertThat(readPersonalInfo.getBody()).hasFieldOrPropertyWithValue("username", ""); //TODO разобраться почему пустое
                    assertThat(readPersonalInfo.getBody()).hasFieldOrPropertyWithValue("photoUrl", "https://a-ttgme.stel.com/i/userpic/320/VkTTUx53J9rIIztcvxv37X3IYETqPzwbxE49mcvMnkTG2dQCLy1UDYt3vICAk-B0.svg");
                }),
                dynamicTest("Получаем два опубликованных курса их трех", () -> {
                    assertThat(jwtToken.get()).as("Токен не был создан. Пользователь не аутентифицирован").isNotNull();

                    var readCoursesInfo = restTemplate.exchange(RequestEntity
                                    .get("/my/courses")
                                    .header("Authorization", "Bearer " + jwtToken)
                                    .build(),
                            new ParameterizedTypeReference<List<CourseSummaryResponse>>() {});

                    assertThat(readCoursesInfo.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200)); //ok
                    assertThat(readCoursesInfo.getBody()).isNotNull();
                    assertThat(readCoursesInfo.getBody().size())
                            .as("Размер списка должен быть равен двум")
                            .isEqualTo(2);
                    assertThat(readCoursesInfo.getBody()).extracting("id")
                                .containsExactlyInAnyOrder(
                                        UUID.fromString("223e4567-e89b-12d3-a456-426614174001"),
                                        UUID.fromString("223e4567-e89b-12d3-a456-426614174002"));
                    assertThat(readCoursesInfo.getBody()).extracting("title")
                                .containsExactlyInAnyOrder("Course 1", "Course 2");
                    assertThat(readCoursesInfo.getBody()).extracting("description")
                                .containsExactlyInAnyOrder("Description for Course 1", "Description for Course 2");
                    assertThat(readCoursesInfo.getBody()).extracting("coverUrl")
                                .containsExactlyInAnyOrder(getS3Url("cover-key-1"), getS3Url("cover-key-2"));
                }),
                //Для простоты проверяем только там, где есть и опубликованные и неопубликованные
                dynamicTest("Получаем детали/темы курса", () -> {
                    assertThat(jwtToken.get()).as("Токен не был создан. Пользователь не аутентифицирован").isNotNull();

                    var readCourseInfo = restTemplate.exchange(RequestEntity
                                    .get("/my/courses/{courseId}", courseId)
                                    .header("Authorization", "Bearer " + jwtToken)
                                    .build(),
                            CourseDetailsResponse.class);

                    assertThat(readCourseInfo.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200)); //ok
                    assertThat(readCourseInfo.getBody()).isNotNull();
                    assertThat(readCourseInfo.getBody()).hasFieldOrPropertyWithValue("id", courseId);
                    assertThat(readCourseInfo.getBody()).hasFieldOrPropertyWithValue("title", "Course 2");
                    assertThat(readCourseInfo.getBody()).hasFieldOrPropertyWithValue("description", "Description for Course 2");
                    assertThat(readCourseInfo.getBody()).hasFieldOrPropertyWithValue("coverUrl", getS3Url("cover-key-2"));
                    assertThat(readCourseInfo.getBody().topics().size())
                            .as("Размер списка тем должен быть равен одному")
                            .isEqualTo(1);
                    assertThat(readCourseInfo.getBody().topics()).extracting("id")
                                .containsExactly(UUID.fromString("323e4567-e89b-12d3-a456-426614174004"));
                    assertThat(readCourseInfo.getBody().topics()).extracting("title")
                                .containsExactly("Topic 2-1");
                    assertThat(readCourseInfo.getBody().topics()).extracting("description")
                                .containsExactly("Description for Topic 2-1");
                    assertThat(readCourseInfo.getBody().topics()).extracting("coverUrl")
                                .containsExactly(getS3Url("cover-key-2-1"));
                }),
                dynamicTest("Получаем опубликованные материалы в теме", () -> {
                    assertThat(jwtToken.get()).as("Токен не был создан. Пользователь не аутентифицирован").isNotNull();

                    var readCourseInfo = restTemplate.exchange(RequestEntity
                                    .get("/my/topics/{topicId}", topicId)
                                    .header("Authorization", "Bearer " + jwtToken)
                                    .build(),
                            TopicDetailsResponse.class);

                    assertThat(readCourseInfo.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200)); //ok
                    assertThat(readCourseInfo.getBody()).isNotNull();
                    assertThat(readCourseInfo.getBody()).hasFieldOrPropertyWithValue("id", topicId);
                    assertThat(readCourseInfo.getBody()).hasFieldOrPropertyWithValue("courseId", courseId);
                    assertThat(readCourseInfo.getBody()).hasFieldOrPropertyWithValue("title", "Topic 2-1");
                    assertThat(readCourseInfo.getBody()).hasFieldOrPropertyWithValue("description", "Description for Topic 2-1");
                    assertThat(readCourseInfo.getBody()).hasFieldOrPropertyWithValue("coverUrl", getS3Url("cover-key-2-1"));
                    assertThat(readCourseInfo.getBody().materials().size())
                            .as("Размер списка должен быть равен одному")
                            .isEqualTo(1);
                    assertThat(readCourseInfo.getBody().materials()).extracting("id")
                                .containsExactly(UUID.fromString("423e4567-e89b-12d3-a456-426614174010"));
                    assertThat(readCourseInfo.getBody().materials()).extracting("title")
                                .containsExactly("Material 2-1-1");
                    assertThat(readCourseInfo.getBody().materials()) .extracting("duration")
                                .containsExactly(120);
                }),
                dynamicTest("Получаем материал для медиаплеера", () -> {
                    assertThat(jwtToken.get()).as("Токен не был создан. Пользователь не аутентифицирован").isNotNull();

                    var readMaterialInfo = restTemplate.exchange(RequestEntity
                                    .get("/my/materials/{materialId}", materialId)
                                    .header("Authorization", "Bearer " + jwtToken)
                                    .build(),
                            MaterialResponse.class);

                    assertThat(readMaterialInfo.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200)); //ok
                    assertThat(readMaterialInfo.getBody()).isNotNull();
                    assertThat(readMaterialInfo.getBody()).hasFieldOrPropertyWithValue("id", materialId);
                    assertThat(readMaterialInfo.getBody()).hasFieldOrPropertyWithValue("topicId", topicId);
                    assertThat(readMaterialInfo.getBody()).hasFieldOrPropertyWithValue("title", "Material 2-1-1");
                    assertThat(readMaterialInfo.getBody()).hasFieldOrPropertyWithValue("coverUrl", getS3Url("cover-key-2-1-1"));
                    assertThat(readMaterialInfo.getBody()).hasFieldOrProperty("mediaUrl");
                    assertThat(new URI(readMaterialInfo.getBody().mediaUrl()).toURL())
                            .hasParameter("X-Amz-Algorithm")
                            .hasParameter("X-Amz-Date")
                            .hasParameter("X-Amz-SignedHeaders")
                            .hasParameter("X-Amz-Credential")
                            .hasParameter("X-Amz-Expires")
                            .hasParameter("X-Amz-Signature");
                })
        );
    }

    @TestFactory
    //TODO переместить в другой тест, где не используется S3
    Collection<DynamicTest> tryBrowseUnpublishedCoursesFlow() {
        final UUID unpublishedCourseId = UUID.fromString("223e4567-e89b-12d3-a456-426614174003");
        final UUID unpublishedTopicId = UUID.fromString("323e4567-e89b-12d3-a456-426614174005");
        final UUID unpublishedMaterialId = UUID.fromString("423e4567-e89b-12d3-a456-426614174011");

        final AtomicReference<String> jwtToken = new AtomicReference<>();

        return List.of(
                dynamicTest("Получаем initData от телеграм и аутентифицируем", () -> {
                    final var createResponse = restTemplate.exchange(RequestEntity
                                    .post("/api/auth/telegram")
                                    .header("Content-Type", "application/json") //TODO а мои контроллеры вкурсе этого заголовка?
                                    .body(TELEGRAM_INIT_DATA),
                            JwtAuthenticationResponse.class);

                    assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200)); //ok
                    assertThat(createResponse.getBody()).isNotNull();
                    assertThat(createResponse.getBody()).hasFieldOrPropertyWithValue("token", VALID_JWT_TOKEN);

                    jwtToken.set(createResponse.getBody().token());
                }),
                dynamicTest("404 Неопубликованный курс", () -> {
                        assertThat(jwtToken).as("Токен не был создан. Пользователь не аутентифицирован").isNotNull();


                    var readCoursesInfo = restTemplate.exchange(RequestEntity
                                        .get("/my/courses/{courseId}", unpublishedCourseId)
                                        .header("Authorization", "Bearer " + jwtToken)
                                        .build(),
                            Void.class);

                        assertThat(readCoursesInfo.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(404)); //ok
                }),
                dynamicTest("404 Неопубликованная тема", () -> {
                    assertThat(jwtToken).as("Токен не был создан. Пользователь не аутентифицирован").isNotNull();

                    var readCoursesInfo = restTemplate.exchange(RequestEntity
                                    .get("/my/topic/{topicId}", unpublishedTopicId)
                                    .header("Authorization", "Bearer " + jwtToken)
                                    .build(),
                            Void.class);

                    assertThat(readCoursesInfo.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(404)); //ok
                }),
                dynamicTest("404 Неопубликованный материал", () -> {
                    assertThat(jwtToken).as("Токен не был создан. Пользователь не аутентифицирован").isNotNull();

                    var readCoursesInfo = restTemplate.exchange(RequestEntity
                                    .get("/my/material/{materialID}", unpublishedMaterialId)
                                    .header("Authorization", "Bearer " + jwtToken)
                                    .build(),
                            Void.class);

                    assertThat(readCoursesInfo.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(404)); //ok
                })
        );
    }
}
