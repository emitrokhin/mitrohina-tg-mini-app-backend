package ru.emitrohin.privateclubbackend.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public abstract class AbstractS3Test extends AbstractIntegrationTest {

    @Container
    protected static LocalStackContainer localStack = new LocalStackContainer(
            DockerImageName.parse("localstack/localstack:3.4")
    );

    protected static S3Client S3_CLIENT;
    protected static final String BUCKET_NAME = "test-bucket";

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("s3.bucket-name", () -> BUCKET_NAME);
        registry.add("s3.region", () -> localStack.getRegion());
        registry.add("s3.access-key", () -> localStack.getAccessKey());
        registry.add("s3.secret-key", () -> localStack.getSecretKey());
        registry.add("s3.endpoint", () -> localStack.getEndpointOverride(S3).toString());
    }

    @BeforeAll
    static void initS3AndCreateBucket() {
        S3_CLIENT = S3Client
                .builder()
                .endpointOverride(localStack.getEndpoint())
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(localStack.getAccessKey(), localStack.getSecretKey())
                        )
                )
                .region(Region.of(localStack.getRegion()))
                .build();

        S3_CLIENT.createBucket(b -> b.bucket(BUCKET_NAME));
        assertThat(S3_CLIENT.listBuckets().buckets().stream().anyMatch(b -> b.name().equals(BUCKET_NAME)))
                .as("Test bucket wasn't created")
                .isTrue();
    }

    protected String getS3Url(String key) {
        return String.format("%s/%s", localStack.getEndpointOverride(S3).toString(), key);
    }
}
