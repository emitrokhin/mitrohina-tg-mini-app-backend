package ru.emitrohin.userapi.integration.process;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import ru.emitrohin.userapi.integration.AbstractIntegrationTest;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

//TODO Раскомментировать тест после поправки модели и таблиц
public class TelegramUserEnrollmentIntegrationTest extends AbstractIntegrationTest {

    //TODO перенести в класс помощник для констант тестовых
    private final static String VALID_JWT_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1MDAwNTY4NDU0IiwiaWF0IjoxNzE3MDAxMDEwLCJleHAiOjE3MTcwMDQ2MTB9.FUAANlm2cIXId-m-9ORlxfFRIu0WcGUVGhSsKlpOack";
    private final static String TELEGRAM_INIT_DATA = "{\"initData\" : \"query_id=AAGGng4qAgAAAIaeDir3Bhb7&user=%7B%22id%22%3A5000568454%2C%22first_name%22%3A%22Evgenii%22%2C%22last_name%22%3A%22%22%2C%22language_code%22%3A%22ru%22%2C%22added_to_attachment_menu%22%3Atrue%2C%22allows_write_to_pm%22%3Atrue%2C%22photo_url%22%3A%22https%3A%5C%2F%5C%2Fa-ttgme.stel.com%5C%2Fi%5C%2Fuserpic%5C%2F320%5C%2FVkTTUx53J9rIIztcvxv37X3IYETqPzwbxE49mcvMnkTG2dQCLy1UDYt3vICAk-B0.svg%22%7D&auth_date=1717001010&hash=4bf601b2e33fe54584bff65f1c343103b8d10b2b6d8fcaf6055b200346c83d34\"}";

    @TestFactory
    Collection<DynamicTest> enrollmentTests() {
        final UUID courseId = UUID.fromString("223e4567-e89b-12d3-a456-426614174002");
        final UUID topicId = UUID.fromString("323e4567-e89b-12d3-a456-426614174004");
        final UUID materialId = UUID.fromString("423e4567-e89b-12d3-a456-426614174010");

        final AtomicReference<String> jwtToken = new AtomicReference<>();
        return List.of(
                dynamicTest("Получаем initData от телеграм и аутентифицируем", () -> {
//                    final var createResponse = restTemplate.exchange(RequestEntity
//                                    .post("/api/auth/telegram")
//                                    .header("Content-Type", "application/json") //TODO а мои контроллеры вкурсе этого заголовка?
//                                    .body(TELEGRAM_INIT_DATA),
//                            JwtAuthenticationResponse.class);
//
//                    assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200)); //ok
//                    assertThat(createResponse.getBody()).isNotNull();
//                    assertThat(createResponse.getBody()).hasFieldOrPropertyWithValue("token", VALID_JWT_TOKEN);
//
//                    jwtToken.set(createResponse.getBody().token());
                }),
                dynamicTest("Получаем пустой список начатых курсов", () -> {
//                    assertThat(jwtToken.get()).as("Токен не был создан. Пользователь не аутентифицирован").isNotNull();
//
//                    var readCoursesInfo = restTemplate.exchange(RequestEntity
//                                    .get("/my/courses/enrolled")
//                                    .header("Authorization", "Bearer " + jwtToken)
//                                    .build(),
//                            List.class);
//
//                    assertThat(readCoursesInfo.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200)); //ok
//                    assertThat(readCoursesInfo.getBody()).isNotNull();
//                    assertThat(readCoursesInfo.getBody().size())
//                            .as("Размер списка должен быть равен нулю")
//                            .isEqualTo(0);
                }),
                dynamicTest("Начинаем курс", () -> {
//                    assertThat(jwtToken.get()).as("Токен не был создан. Пользователь не аутентифицирован").isNotNull();
//
//                    var readCoursesInfo = restTemplate.exchange(RequestEntity
//                                    .post("/my/courses/{courseId}/enroll", courseId)
//                                    .header("Authorization", "Bearer " + jwtToken)
//                                    .build(),
//                            EnrollmentResponse.class);
//
//                    assertThat(readCoursesInfo.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200)); //ok
//                    assertThat(readCoursesInfo.getBody()).isNotNull();
//                    assertThat(readCoursesInfo.getBody()).hasFieldOrProperty("enrollmentId");
                }),
                dynamicTest("Получаем список начатых курсов из одного", () -> {
//                    assertThat(jwtToken.get()).as("Токен не был создан. Пользователь не аутентифицирован").isNotNull();
//
//                    var readCoursesInfo = restTemplate.exchange(RequestEntity
//                                    .get("/my/courses/enrolled")
//                                    .header("Authorization", "Bearer " + jwtToken)
//                                    .build(),
//                            List.class);
//
//                    assertThat(readCoursesInfo.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200)); //ok
//                    assertThat(readCoursesInfo.getBody()).isNotNull();
//                    assertThat(readCoursesInfo.getBody().size())
//                            .as("Размер списка должен быть равен одному")
//                            .isEqualTo(1);
                }),
                dynamicTest("Проверяем начатые темы", () -> {
                    //TODO в ответе CourseDetailsResponse должны быть данные о том, что он начат
                    //TODO в ответе CourseDetailsResponse.TopicSummary должны быть данные о начатых темах
                })
        );
    }
}
