package ru.emitrohin.privateclubbackend.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class S3Configuration {

    @Value("${s3.url}")
    private String s3url;

    @Value("${s3.accessKey}")
    private String accessKey;

    @Value("${s3.secretKey}")
    private String secretKey;

    @Value("${s3.region}")
    private String region;

    @Value("${s3.bucketName}")
    private String bucketName;

    private AwsBasicCredentials credentials;

    @PostConstruct
    public void init() {
        credentials = AwsBasicCredentials.create(accessKey, secretKey);
    }

    @Bean
    @Primary
    public S3Presigner s3Presigner() throws URISyntaxException {
        return S3Presigner.builder()
                .endpointOverride(new URI(s3url))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.of(region))
                .build();
    }

    @Bean
    @Primary
    public S3Client s3Client() throws URISyntaxException {
        return S3Client.builder()
                .endpointOverride(new URI(s3url))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.of(region))
                .build();
    }

    @Bean
    public String s3url() {
        return s3url;
    }

    @Bean
    public String bucketName() {
        return bucketName;
    }
}
