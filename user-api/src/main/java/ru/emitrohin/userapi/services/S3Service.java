package ru.emitrohin.userapi.services;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.net.URL;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Presigner s3Presigner;

    private final String endpoint;

    private final String bucketName;

    @Named("generatePresignedUrl")
    public String generatePresignedUrl(String objectKey) {
        URL url;
        try (s3Presigner) {

            var getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            var getObjectPresignRequest = GetObjectPresignRequest.builder()
                    .getObjectRequest(getObjectRequest)
                    .signatureDuration(Duration.ofHours(1)) //TODO уменьшить срок
                    .build();

            url = s3Presigner.presignGetObject(getObjectPresignRequest).url();
        }

        return url.toString();
    }

    @Named("generatePermanentUrl")
    public String generatePermanentUrl(String objectKey) {
        return String.format("%s/%s", endpoint, objectKey);
    }
}