package ru.emitrohin.privateclubbackend.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Presigner s3Presigner;

    private final S3Client s3Client;

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

    public String uploadPrivateFile(MultipartFile file) {
        return uploadFileWithAcl(file, ObjectCannedACL.PRIVATE);
    }

    public String uploadPublicFile(MultipartFile file) {
        return uploadFileWithAcl(file, ObjectCannedACL.PUBLIC_READ);
    }

    private String uploadFileWithAcl(MultipartFile file, ObjectCannedACL acl) {
        String objectKey = generateObjectKey();
        uploadFileToS3(objectKey, file);
        setObjectRights(objectKey, acl);
        return objectKey;
    }

    private String generateObjectKey() {
        return UUID.randomUUID().toString();
    }

    private void uploadFileToS3(String objectKey, MultipartFile file) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(file.getContentType())
                .build();
        //TODO log successful operetaion
        try {
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException e) {
            throw new RuntimeException("Unable to upload file ", e);
        }
    }

    private void setObjectRights(String objectKey, ObjectCannedACL right) {
        PutObjectAclRequest request = PutObjectAclRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .acl(right)
                .build();

        s3Client.putObjectAcl(request);
    }

    public void deleteFile(String objectKey) {
        var deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }
}