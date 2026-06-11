package com.mickey.dinggading.infra.minio;

import java.io.InputStream;
import java.util.Map;

public interface MinioService {

    String generatePresignedUrlMyBucket(String objectName);

    /**
     * 버킷이 존재하는지 확인하고, 없으면 생성합니다.
     *
     * @param bucketName 버킷 이름
     */
    void ensureBucketExists(String bucketName);

    /**
     * 파일을 업로드합니다.
     *
     * @param bucketName  버킷 이름
     * @param objectName  파일 이름
     * @param inputStream 파일 스트림
     * @param size        파일 크기
     * @param contentType 컨텐츠 타입
     * @param metadata    메타데이터
     * @return 업로드된 객체 이름
     */
    String uploadObject(String bucketName, String objectName, InputStream inputStream,
                        long size, String contentType, Map<String, String> metadata);

    /**
     * 객체에 대한 프리사인드 URL을 생성합니다.
     *
     * @param bucketName    버킷 이름
     * @param objectName    파일 이름
     * @param expirySeconds 만료 시간(초)
     * @return 프리사인드 URL
     */
    String generatePresignedUrl(String bucketName, String objectName, int expirySeconds);

    /**
     * 객체를 삭제합니다.
     *
     * @param bucketName 버킷 이름
     * @param objectName 파일 이름
     */
    void removeObject(String bucketName, String objectName);
}