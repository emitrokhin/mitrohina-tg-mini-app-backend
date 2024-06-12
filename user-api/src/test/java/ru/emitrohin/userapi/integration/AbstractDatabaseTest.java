package ru.emitrohin.userapi.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class AbstractDatabaseTest {

    private static final String POSTGRES_DOCKER_VERSION = "postgres:16-alpine";

    protected static final Logger log = LoggerFactory.getLogger("integration-test");

    //Запускать вручную без аннотаций, иначе withReuse работать не будет
    protected static PostgreSQLContainer<?> container = new PostgreSQLContainer<>(POSTGRES_DOCKER_VERSION)
            .withDatabaseName("mini-app")
            .withReuse(true);

    static {
        container.start();
    }

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> container.getJdbcUrl() + "&stringtype=unspecified");
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
    }
}