package com.mickey.dinggading.infra.minio;

import org.springframework.web.multipart.MultipartFile;

public interface AudioFileService {

    /**
     * 오디오 파일을 업로드합니다.
     *
     * @param file       업로드할 오디오 파일
     * @param recordType 녹음 유형 (CHALLENGE, LIVE_HOUSE, PRACTICE)
     * @param memberId   사용자 ID
     * @return 저장된 파일명
     */
    String uploadAudioFile(MultipartFile file, String recordType, String memberId);

    /**
     * 오디오 파일의 URL을 가져옵니다.
     *
     * @param filename 파일명
     * @return 파일 URL
     */
    String getAudioFileUrl(String filename);

    /**
     * 오디오 파일의 URL을 지정된 만료 시간으로 가져옵니다.
     *
     * @param filename      파일명
     * @param expirySeconds URL 만료 시간(초)
     * @return 파일 URL
     */
    String getAudioFileUrl(String filename, int expirySeconds);

    /**
     * 오디오 파일을 삭제합니다.
     *
     * @param filename 삭제할 파일명
     */
    void deleteAudioFile(String filename);
}