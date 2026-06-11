package com.mickey.dinggading.infra.minio;

import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import java.io.InputStream;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioServiceImpl implements MinioService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    @Override
    public String generatePresignedUrlMyBucket(String objectName) {
        return generatePresignedUrl(
                minioProperties.getBucketName(),
                objectName,
                3600
        );
    }

    @Override
    public void ensureBucketExists(String bucketName) {
        try {
            boolean bucketExists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );

            if (!bucketExists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .build()
                );
                log.info("Created bucket: {}", bucketName);
            }
        } catch (Exception e) {
            log.error("Error ensuring bucket exists: {}", bucketName, e);
            throw new MinioOperationException("Failed to ensure bucket exists", e);
        }
    }

    @Override
    public String uploadObject(String bucketName, String objectName, InputStream inputStream,
                               long size, String contentType, Map<String, String> metadata) {
        try {
            // 버킷 존재 확인
            ensureBucketExists(bucketName);

            // 객체 업로드
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, size, -1)
                            .contentType(contentType)
                            .userMetadata(metadata)
                            .build()
            );

            log.info("Uploaded object {} to bucket {}", objectName, bucketName);
            return objectName;
        } catch (Exception e) {
            log.error("Error uploading object: {} to bucket: {}", objectName, bucketName, e);
            throw new MinioOperationException("Failed to upload object", e);
        }
    }

    @Override
    public String generatePresignedUrl(String bucketName, String objectName, int expirySeconds) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(expirySeconds)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error generating presigned URL for object: {} in bucket: {}",
                    objectName, bucketName, e);
            throw new MinioOperationException("Failed to generate presigned URL", e);
        }
    }

    @Override
    public void removeObject(String bucketName, String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            log.info("Removed object {} from bucket {}", objectName, bucketName);
        } catch (Exception e) {
            log.error("Error removing object: {} from bucket: {}", objectName, bucketName, e);
            throw new MinioOperationException("Failed to remove object", e);
        }
    }
}