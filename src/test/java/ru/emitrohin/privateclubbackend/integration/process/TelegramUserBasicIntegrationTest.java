package ru.emitrohin.privateclubbackend.integration.process;

import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.RequestEntity;
import org.springframework.test.context.ActiveProfiles;
import ru.emitrohin.privateclubbackend.config.JwtFixedClockConfig;
import ru.emitrohin.privateclubbackend.config.TestSecurityConfiguration;
import ru.emitrohin.privateclubbackend.dto.response.JwtAuthenticationResponse;
import ru.emitrohin.privateclubbackend.dto.response.UserResponse;
import ru.emitrohin.privateclubbackend.integration.AbstractIntegrationTest;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 1) Получаем initData от телеграм, авторизуем пользователя и выдаем jwt
 1а) Получаем собственные данные
 2) Получаем доступные
 2а) курсы/курс
 2б) темы/материалы
 2в) материалы для воспроизведения
 3) Начинаем
 3а) курс
 3б) тему
 3в) материал
 4) Получаем список начатых
 3а) курсов
 3б) тем
 3в) материалов
 TODO: завершить / список завершенных
 */

@Import(value = {TestSecurityConfiguration.class, JwtFixedClockConfig.class})
@ActiveProfiles("test") //todo разобраться, почему не получилось переопределить Clock без профилей @primary не работает в тест конфиге
public class TelegramUserBasicIntegrationTest extends AbstractIntegrationTest {

    final private String validJwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1MDAwNTY4NDU0IiwiaWF0IjoxNzE3MDAxMDEwLCJleHAiOjE3MTcwMDQ2MTB9.FUAANlm2cIXId-m-9ORlxfFRIu0WcGUVGhSsKlpOack";
    final private String telegramInitData = "{\"initData\" : \"query_id=AAGGng4qAgAAAIaeDir3Bhb7&user=%7B%22id%22%3A5000568454%2C%22first_name%22%3A%22Evgenii%22%2C%22last_name%22%3A%22%22%2C%22language_code%22%3A%22ru%22%2C%22added_to_attachment_menu%22%3Atrue%2C%22allows_write_to_pm%22%3Atrue%2C%22photo_url%22%3A%22https%3A%5C%2F%5C%2Fa-ttgme.stel.com%5C%2Fi%5C%2Fuserpic%5C%2F320%5C%2FVkTTUx53J9rIIztcvxv37X3IYETqPzwbxE49mcvMnkTG2dQCLy1UDYt3vICAk-B0.svg%22%7D&auth_date=1717001010&hash=4bf601b2e33fe54584bff65f1c343103b8d10b2b6d8fcaf6055b200346c83d34\"}";
    private String jwtToken;
    private String userId;

    @Before
    public void insertS3Data() {
        localStack.execInContainer("awslocal", "s3", "mb", "s3://" + BUCKET_NAME);
    }

    @TestFactory
    Collection<DynamicTest> basicFlow() {
        return List.of(
                dynamicTest("1) Получаем initData от телеграми и аутентифицируем", () -> {
                    final var createResponse = restTemplate.exchange(RequestEntity
                                    .post("/api/auth/telegram")
                                    .header("Content-Type", "application/json") //TODO а мои контроллеры вкурсе этого заголовка?
                                    .body(telegramInitData),
                                     JwtAuthenticationResponse.class);

                    assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200)); //ok
                    assertThat(createResponse.getBody()).isNotNull();
                    assertThat(createResponse.getBody()).hasFieldOrPropertyWithValue("token", validJwtToken);

                    jwtToken = createResponse.getBody().token();
                    log.info("Authorizing telegram user 5000568454 with jwt token {}", jwtToken);
                }),
                dynamicTest("1а) Получаем данные о себе", () -> {
                    assertThat(jwtToken).as("Токен не был создан. Пользователь не аутентифицирован").isNotNull();
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
                })
        );
    }
}
