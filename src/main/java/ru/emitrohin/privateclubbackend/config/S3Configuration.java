package ru.emitrohin.privateclubbackend.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.emitrohin.privateclubbackend.config.properties.S3Properties;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
@EnableConfigurationProperties(S3Properties.class)
@RequiredArgsConstructor
public class S3Configuration {

    private final S3Properties properties;

    private AwsBasicCredentials credentials;

    @PostConstruct
    public void init() {
        credentials = AwsBasicCredentials.create(properties.accessKey(), properties.accessKey() );
    }

    @Bean
    public S3Presigner s3Presigner() throws URISyntaxException {
        return S3Presigner.builder()
                .endpointOverride(new URI(properties.endpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.of(properties.region()))
                .build();
    }

    @Bean
    public S3Client s3Client() throws URISyntaxException {
        return S3Client.builder()
                .endpointOverride(new URI(properties.endpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.of(properties.region()))
                .build();
    }

    @Bean
    public String endpoint() {
        return properties.endpoint();
    }

    @Bean
    public String bucketName() {
        return properties.bucketName();
    }
}
