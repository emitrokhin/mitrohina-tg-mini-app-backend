package ru.emitrohin.privateclubbackend.integration.crud;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.RequestEntity;
import ru.emitrohin.privateclubbackend.dto.response.JwtAuthenticationResponse;
import ru.emitrohin.privateclubbackend.dto.response.UserResponse;
import ru.emitrohin.privateclubbackend.integration.AbstractS3Test;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class UserCrudIntegrationTest extends AbstractS3Test {

    private final static String VALID_JWT_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1MjNlNDU2Ny1lODliLTEyZDMtYTQ1Ni00MjY2MTQxNzQwMDQiLCJpYXQiOjE3MTcwMDEwMTAsImV4cCI6MTcxNzAwNDYxMH0.oAQ25G2twbUKVPaSlcxkqQUchJBNVTLqSoOWIqkqwGo";
    private final static UUID USER_ID = UUID.fromString("065a27d3-c1ef-448b-ae02-b29066d3e650");

    @BeforeEach
    void clearDatabase(@Autowired Flyway flyway) {
        flyway.clean();
        flyway.migrate();
    }

    @TestFactory
    Collection<DynamicTest> authAndUserReadDeleteTest() {
        final AtomicReference<String> jwtToken = new AtomicReference<>();

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
                    assertThat(createAdminResponse.getBody()).hasFieldOrPropertyWithValue("token", VALID_JWT_TOKEN);

                    jwtToken.set(createAdminResponse.getBody().token());
                    log.info("Authorizing admin with jwt token {}", jwtToken);

                }),
                dynamicTest("Читаем данные пользователя по id", () -> {
                    assertThat(jwtToken.get()).as("Токен не был создан. Админ не аутентифицирован").isNotNull();

                    final var readByIdResponse = restTemplate.exchange(RequestEntity
                                    .get("/admin/users/{userId}", USER_ID)
                                    .header("Content-Type", "application/json")
                                    .header("Authorization", "Bearer " + jwtToken)
                                    .build(),
                            UserResponse.class);

                    assertThat(readByIdResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200)); //ok
                    assertThat(readByIdResponse.getBody()).isNotNull();
                    assertThat(readByIdResponse.getBody()).hasFieldOrPropertyWithValue("id", USER_ID);
                    assertThat(readByIdResponse.getBody()).hasFieldOrPropertyWithValue("telegramId", 9934567891L);
                    assertThat(readByIdResponse.getBody()).hasFieldOrPropertyWithValue("firstName", "Jane");
                    assertThat(readByIdResponse.getBody()).hasFieldOrPropertyWithValue("lastName", "Doe");
                    assertThat(readByIdResponse.getBody()).hasFieldOrPropertyWithValue("username", null);
                    assertThat(readByIdResponse.getBody()).hasFieldOrPropertyWithValue("photoUrl", null);

                    log.info("Got user with id {} and telegramId {}", USER_ID, readByIdResponse.getBody().telegramId());
                }),
                dynamicTest("Получить список пользователей из трех", () -> {
                    assertThat(jwtToken.get()).as("Токен не был создан. Админ не аутентифицирован").isNotNull();

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
                    assertThat(jwtToken.get()).as("Токен не был создан. Админ не аутентифицирован").isNotNull();

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
                    assertThat(jwtToken.get()).as("Токен не был создан. Админ не аутентифицирован").isNotNull();

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
