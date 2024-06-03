package ru.emitrohin.privateclubbackend.integration;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.emitrohin.privateclubbackend.config.FlywayMigrationConfig;
import ru.emitrohin.privateclubbackend.config.JwtFixedClockConfig;
import ru.emitrohin.privateclubbackend.config.TestSecurityConfiguration;
import ru.emitrohin.privateclubbackend.util.S3Utils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.UUID;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({ TestSecurityConfiguration.class,
          JwtFixedClockConfig.class,
          FlywayMigrationConfig.class })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
public abstract class AbstractIntegrationTest extends AbstractDatabaseTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected S3Utils s3Utils;


    private static final DockerImageName localStackImage = DockerImageName.parse("localstack/localstack:3.4");

    protected static LocalStackContainer localStack = new LocalStackContainer(localStackImage).withServices(S3);

    static final String BUCKET_NAME = UUID.randomUUID().toString();

    static {
        localStack.start();
    }

    private static S3Client s3Client;

    @BeforeAll
    public static void setUpS3() {
        s3Client = S3Client.builder()
                .endpointOverride(localStack.getEndpointOverride(LocalStackContainer.Service.S3))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(localStack.getAccessKey(), localStack.getSecretKey())))
                .region(Region.of(localStack.getRegion()))
                .build();
    }

    @BeforeEach
    void setUpRest() {
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory("http://localhost:" + port));
    }

    @BeforeEach
    void clearDatabase(@Autowired Flyway flyway) {
        flyway.clean();
        flyway.migrate();
    }

    @DynamicPropertySource
    static void s3Properties(DynamicPropertyRegistry registry) {
        registry.add("app.bucket", () -> BUCKET_NAME);
        registry.add("s3.region",
                () -> localStack.getRegion()
        );
        registry.add(
                "spring.cloud.aws.credentials.access-key",
                () -> localStack.getAccessKey()
        );
        registry.add(
                "spring.cloud.aws.credentials.secret-key",
                () -> localStack.getSecretKey()
        );
        registry.add(
                "spring.cloud.aws.s3.endpoint",
                () -> localStack.getEndpointOverride(S3).toString()
        );
        registry.add(
                "spring.cloud.aws.sqs.endpoint",
                () -> localStack.getEndpointOverride(SQS).toString()
        );
    }
}
