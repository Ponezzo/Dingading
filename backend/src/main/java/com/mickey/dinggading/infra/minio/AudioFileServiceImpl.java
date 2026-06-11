package com.mickey.dinggading.infra.minio;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class AudioFileServiceImpl implements AudioFileService {

    // 허용되는 오디오 파일 포맷 정의
    private static final String[] ALLOWED_AUDIO_FORMATS = {
            "audio/mpeg", "audio/mp3", "audio/wav", "audio/x-wav"
    };
    // 최대 파일 크기 (50MB)
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024;
    // 기본 URL 만료 시간 (1시간)
    private static final int DEFAULT_EXPIRY_SECONDS = 3600;
    private final MinioService minioService;
    private final MinioProperties minioProperties;

    @Override
    public String uploadAudioFile(MultipartFile file, String recordType, String memberId) {
        try {
            // 파일 유효성 검사
            validateAudioFile(file);

            // 파일명 생성 (타입_사용자ID_UUID_원본파일명)
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String filename = String.format("%s_%s_%s_%s",
                    recordType, memberId, UUID.randomUUID(), originalFilename);

            // 메타데이터 설정
            Map<String, String> metadata = new HashMap<>();
            metadata.put("Content-Type", file.getContentType());
            metadata.put("Original-Filename", originalFilename);
            metadata.put("Record-Type", recordType);
            metadata.put("Member-Id", memberId);

            // 파일 업로드
            return minioService.uploadObject(
                    minioProperties.getBucketName(),
                    filename,
                    file.getInputStream(),
                    file.getSize(),
                    file.getContentType(),
                    metadata
            );
        } catch (IOException e) {
            log.error("Failed to process audio file", e);
            throw new AudioFileValidationException("Failed to process audio file", e);
        }
    }

    @Override
    public String getAudioFileUrl(String filename) {
        return getAudioFileUrl(filename, DEFAULT_EXPIRY_SECONDS);
    }

    @Override
    public String getAudioFileUrl(String filename, int expirySeconds) {
        if (expirySeconds <= 0) {
            expirySeconds = DEFAULT_EXPIRY_SECONDS;
        }

        return minioService.generatePresignedUrl(
                minioProperties.getBucketName(),
                filename,
                expirySeconds
        );
    }

    @Override
    public void deleteAudioFile(String filename) {
        minioService.removeObject(minioProperties.getBucketName(), filename);
    }

    /**
     * 오디오 파일 유효성을 검사합니다.
     *
     * @param file 검사할 파일
     */
    private void validateAudioFile(MultipartFile file) {
        // 파일 존재 여부 확인
        if (file.isEmpty()) {
            throw new AudioFileValidationException("File is empty");
        }

        // 파일 크기 확인
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new AudioFileValidationException(
                    String.format("File size exceeds maximum allowed size of %d MB",
                            MAX_FILE_SIZE / (1024 * 1024))
            );
        }

        // 파일 형식 확인
        String contentType = file.getContentType();
        if (contentType == null || !Arrays.asList(ALLOWED_AUDIO_FORMATS).contains(contentType)) {
            throw new AudioFileValidationException(
                    String.format("Invalid audio file format. Allowed formats are: %s",
                            String.join(", ", ALLOWED_AUDIO_FORMATS))
            );
        }
    }
}