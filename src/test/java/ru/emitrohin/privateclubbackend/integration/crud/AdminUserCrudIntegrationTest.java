package ru.emitrohin.privateclubbackend.integration.crud;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.RequestEntity;
import org.springframework.test.context.ActiveProfiles;
import ru.emitrohin.privateclubbackend.dto.response.AdminUserResponse;
import ru.emitrohin.privateclubbackend.dto.response.JwtAuthenticationResponse;
import ru.emitrohin.privateclubbackend.integration.AbstractIntegrationTest;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@ActiveProfiles("test")
public class AdminUserCrudIntegrationTest extends AbstractIntegrationTest {

    final private String validJwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1MjNlNDU2Ny1lODliLTEyZDMtYTQ1Ni00MjY2MTQxNzQwMDQiLCJpYXQiOjE3MTcwMDEwMTAsImV4cCI6MTcxNzAwNDYxMH0.oAQ25G2twbUKVPaSlcxkqQUchJBNVTLqSoOWIqkqwGo";
    private String jwtToken;
    private UUID newAdminId;

    @TestFactory
    Collection<DynamicTest> authAndAdminUserCRUDTest() {
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
                dynamicTest("Создать админа", () -> {
                    assertThat(jwtToken).as("Токен не был создан. Админ не аутентифицирован").isNotNull();
                    final var createRequest = """
                        {
                            "username": "newAdmin",
                            "password": "somePassword"
                        }
                        """;

                    final var createResponse = restTemplate.exchange(RequestEntity
                                    .post("/admin/users/admins")
                                    .header("Content-Type", "application/json")
                                    .header("Authorization", "Bearer " + jwtToken)
                                    .body(createRequest),
                            AdminUserResponse.class);

                    assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200)); //ok
                    assertThat(createResponse.getBody()).isNotNull();
                    assertThat(createResponse.getBody()).hasFieldOrProperty("id");
                    assertThat(createResponse.getBody()).hasFieldOrPropertyWithValue("username", "newAdmin");

                    newAdminId = createResponse.getBody().id();
                    log.info("Creating admin with id {}", newAdminId);
                }),
                dynamicTest("Получить данные админа по id", () -> {
                    assertThat(jwtToken).as("Токен не был создан. Админ не аутентифицирован").isNotNull();
                    assertThat(newAdminId).as("Id не существует. Новый админ не был создан").isNotNull();

                    final var readByIdResponse = restTemplate.exchange(RequestEntity
                                    .get("/admin/users/admins/{id}", newAdminId)
                                    .header("Content-Type", "application/json")
                                    .header("Authorization", "Bearer " + jwtToken)
                                    .build(),
                            AdminUserResponse.class);

                    assertThat(readByIdResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200)); //ok
                    assertThat(readByIdResponse.getBody()).isNotNull();
                    assertThat(readByIdResponse.getBody()).hasFieldOrProperty("id");
                    assertThat(readByIdResponse.getBody()).hasFieldOrPropertyWithValue("username", "newAdmin");
                }),
                dynamicTest("Получить списков двух админов", () -> {
                    assertThat(jwtToken).as("Токен не был создан. Админ не аутентифицирован").isNotNull();

                    var readAllAdminsResponse = restTemplate.exchange(RequestEntity
                                    .get("/admin/users/admins")
                                    .header("Authorization", "Bearer " + jwtToken)
                                    .build(),
                            new ParameterizedTypeReference<List<AdminUserResponse>>() {});

                    assertThat(readAllAdminsResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200)); //ok
                    assertThat(readAllAdminsResponse.getBody()).isNotNull();
                    assertThat(readAllAdminsResponse.getBody().size()).as("Размер списка должен быть равен двум").isEqualTo(2);
                    assertThat(readAllAdminsResponse.getBody()).extracting("username").containsExactlyInAnyOrder("newAdmin", "admin");
                }),
                dynamicTest("Обновить пароль админа", () -> {
                    assertThat(jwtToken).as("Токен не был создан. Админ не аутентифицирован").isNotNull();
                    assertThat(newAdminId).as("Id не существует. Новый админ не был создан").isNotNull();

                    final var passwordUpdateRequest = """
                        {
                            "password": "newPassword"
                        }
                        """;

                    final var updateResponse = restTemplate.exchange(RequestEntity
                                    .put("/admin/users/admins/{id}/update-password", newAdminId)
                                    .header("Content-Type", "application/json")
                                    .header("Authorization", "Bearer " + jwtToken)
                                    .body(passwordUpdateRequest),
                            AdminUserResponse.class);

                    assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200)); //ok
                    assertThat(updateResponse.getBody()).isNotNull();
                    assertThat(updateResponse.getBody()).hasFieldOrPropertyWithValue("username", "newAdmin");
                }),
                dynamicTest("Удалить админа", () -> {
                    assertThat(jwtToken).as("Токен не был создан. Админ не аутентифицирован").isNotNull();
                    assertThat(newAdminId).as("Id не существует. Новый админ не был создан").isNotNull();


                    final var updateResponse = restTemplate.exchange(RequestEntity
                                    .delete("/admin/users/admins/{id}", newAdminId)
                                    .header("Content-Type", "application/json")
                                    .header("Authorization", "Bearer " + jwtToken)
                                    .build(),
                            AdminUserResponse.class);

                    assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204)); //no content
                })
        );
    }
}
