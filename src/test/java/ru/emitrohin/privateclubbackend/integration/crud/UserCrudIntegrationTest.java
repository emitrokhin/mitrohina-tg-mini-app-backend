package ru.emitrohin.privateclubbackend.integration.crud;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.RequestEntity;
import org.springframework.test.context.ActiveProfiles;
import ru.emitrohin.privateclubbackend.config.JwtFixedClockConfig;
import ru.emitrohin.privateclubbackend.config.TestSecurityConfiguration;
import ru.emitrohin.privateclubbackend.dto.response.AdminUserResponse;
import ru.emitrohin.privateclubbackend.dto.response.JwtAuthenticationResponse;
import ru.emitrohin.privateclubbackend.dto.response.UserResponse;
import ru.emitrohin.privateclubbackend.integration.AbstractIntegrationTest;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@Import(value = {TestSecurityConfiguration.class, JwtFixedClockConfig.class})
@ActiveProfiles("test")
public class UserCrudIntegrationTest extends AbstractIntegrationTest {

    final private String validJwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1MjNlNDU2Ny1lODliLTEyZDMtYTQ1Ni00MjY2MTQxNzQwMDQiLCJpYXQiOjE3MTcwMDEwMTAsImV4cCI6MTcxNzAwNDYxMH0.oAQ25G2twbUKVPaSlcxkqQUchJBNVTLqSoOWIqkqwGo";
    final private UUID userId = UUID.fromString("065a27d3-c1ef-448b-ae02-b29066d3e650");

    private String jwtToken;

    @BeforeEach
    void clearDatabase(@Autowired Flyway flyway) {
        flyway.clean();
        flyway.migrate();
    }

    @TestFactory
    Collection<DynamicTest> authAndUserReadDeleteTest() {
        return List.of(
                dynamicTest("Стартовый админ авторизуется", () -> {
                    final var loginPasswordRequest = """
                        {
                            "username": "admin",
                            "password": "PASSWORD"
                        }
                        """;

                    final var createAdminResponse = restTemplate.exchange(RequestEntity
                                    .post("/api/auth/admin")
                                    .header("Content-Type", "application/json")
                                    .body(loginPasswordRequest),
                            JwtAuthenticationResponse.class);

                    assertThat(createAdminResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200)); //ok
                    assertThat(createAdminResponse.getBody()).isNotNull();
                    assertThat(createAdminResponse.getBody()).hasFieldOrPropertyWithValue("token", validJwtToken);

                    jwtToken = createAdminResponse.getBody().token();
                    log.info("Authorizing admin with jwt token {}", jwtToken);

                }),
                dynamicTest("Читаем данные пользователя по id", () -> {
                    assertThat(jwtToken).as("Токен не был создан. Админ не аутентифицирован").isNotNull();

                    final var readByIdResponse = restTemplate.exchange(RequestEntity
                                    .get("/admin/users/{userId}", userId)
                                    .header("Content-Type", "application/json")
                                    .header("Authorization", "Bearer " + jwtToken)
                                    .build(),
                            UserResponse.class);

                    assertThat(readByIdResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200)); //ok
                    assertThat(readByIdResponse.getBody()).isNotNull();
                    assertThat(readByIdResponse.getBody()).hasFieldOrPropertyWithValue("id", userId);
                    assertThat(readByIdResponse.getBody()).hasFieldOrPropertyWithValue("telegramId", 9934567891L);
                    assertThat(readByIdResponse.getBody()).hasFieldOrPropertyWithValue("firstName", "Jane");
                    assertThat(readByIdResponse.getBody()).hasFieldOrPropertyWithValue("lastName", "Doe");
                    assertThat(readByIdResponse.getBody()).hasFieldOrPropertyWithValue("username", null);
                    assertThat(readByIdResponse.getBody()).hasFieldOrPropertyWithValue("photoUrl", null);

                    log.info("Got user with id {} and telegramId {}", userId, readByIdResponse.getBody().telegramId());
                }),
                dynamicTest("Получить список пользователей из трех", () -> {
                    assertThat(jwtToken).as("Токен не был создан. Админ не аутентифицирован").isNotNull();

                    final var readAllUsersResponse = restTemplate.exchange(RequestEntity
                                    .get("/admin/users")
                                    .header("Content-Type", "application/json")
                                    .header("Authorization", "Bearer " + jwtToken)
                                    .build(),
                            new ParameterizedTypeReference<List<UserResponse>>() {});

                    assertThat(readAllUsersResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200)); //ok
                    assertThat(readAllUsersResponse.getBody()).isNotNull();
                    assertThat(readAllUsersResponse.getBody().size()).as("Размер списка должен быть равен трем").isEqualTo(3);
                    assertThat(readAllUsersResponse.getBody()).extracting("firstName").containsExactlyInAnyOrder("John", "Jane", "Alice");
                }),
                dynamicTest("Удалить пользователя по id", () -> {
                    assertThat(jwtToken).as("Токен не был создан. Админ не аутентифицирован").isNotNull();

                    final var updateResponse = restTemplate.exchange(RequestEntity
                                    .delete("/admin/users/{id}", UUID.fromString("62991587-9363-44a5-bf2f-658471549e43"))
                                    .header("Content-Type", "application/json")
                                    .header("Authorization", "Bearer " + jwtToken)
                                    .build(),
                            Void.class);

                    assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204)); //ok
                    assertThat(updateResponse.getBody()).isNull();
                }),
                dynamicTest("Получить список пользователей из двух после удаления", () -> {
                    assertThat(jwtToken).as("Токен не был создан. Админ не аутентифицирован").isNotNull();

                    final var readAllUsersResponse = restTemplate.exchange(RequestEntity
                                    .get("/admin/users")
                                    .header("Content-Type", "application/json")
                                    .header("Authorization", "Bearer " + jwtToken)
                                    .build(),
                            new ParameterizedTypeReference<List<UserResponse>>() {});

                    assertThat(readAllUsersResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200)); //ok
                    assertThat(readAllUsersResponse.getBody()).isNotNull();
                    assertThat(readAllUsersResponse.getBody().size()).as("Размер списка должен быть равен двум").isEqualTo(2);
                    assertThat(readAllUsersResponse.getBody()).extracting("firstName").containsExactlyInAnyOrder("John", "Jane");
                })
        );
    }
}
