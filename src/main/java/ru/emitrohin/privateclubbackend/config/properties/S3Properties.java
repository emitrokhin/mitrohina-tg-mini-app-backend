package ru.emitrohin.privateclubbackend.config.properties;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "s3")
@Validated
public record S3Properties (
        @NotEmpty String endpoint,
        @NotEmpty String accessKey,
        @NotEmpty String secretKey,
        @NotEmpty String region,
        @NotEmpty String bucketName
){
}