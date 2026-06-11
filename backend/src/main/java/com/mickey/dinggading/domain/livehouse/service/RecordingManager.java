package com.mickey.dinggading.domain.livehouse.service;

import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.openvidu.java.client.Recording;
import io.openvidu.java.client.RecordingProperties;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 녹화 관련 메서드들
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RecordingManager {

    private final OpenVidu openVidu;

    /**
     * 세션 녹화 시작
     *
     * @param sessionId 세션 ID
     * @param name      녹화 이름
     * @return 녹화 객체
     */
    public Recording startRecording(String sessionId, String name) {
        try {
            RecordingProperties properties = new RecordingProperties.Builder()
                    .name(name)
                    .build();
            Recording recording = openVidu.startRecording(sessionId, properties);
            log.info("녹화 시작: 세션={}, 녹화ID={}", sessionId, recording.getId());
            return recording;
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            log.error("녹화 시작 중 오류 발생", e);
            throw new RuntimeException("녹화를 시작할 수 없습니다", e);
        }
    }

    /**
     * 세션 녹화 중지
     *
     * @param recordingId 녹화 ID
     * @return 완료된 녹화 객체
     */
    public Recording stopRecording(String recordingId) {
        try {
            Recording recording = openVidu.stopRecording(recordingId);
            log.info("녹화 중지: 녹화ID={}", recordingId);
            return recording;
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            log.error("녹화 중지 중 오류 발생", e);
            throw new RuntimeException("녹화를 중지할 수 없습니다", e);
        }
    }

    /**
     * 녹화 목록 가져오기
     *
     * @return 녹화 목록
     */
    public List<Recording> getRecordings() {
        try {
            return openVidu.listRecordings();
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            log.error("녹화 목록 조회 중 오류 발생", e);
            throw new RuntimeException("녹화 목록을 가져올 수 없습니다", e);
        }
    }

    /**
     * 녹화 정보 가져오기
     *
     * @param recordingId 녹화 ID
     * @return 녹화 객체
     */
    public Recording getRecording(String recordingId) {
        try {
            return openVidu.getRecording(recordingId);
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            log.error("녹화 정보 조회 중 오류 발생", e);
            throw new RuntimeException("녹화 정보를 가져올 수 없습니다", e);
        }
    }

    /**
     * 녹화 삭제
     *
     * @param recordingId 녹화 ID
     */
    public void deleteRecording(String recordingId) {
        try {
            openVidu.deleteRecording(recordingId);
            log.info("녹화 삭제: 녹화ID={}", recordingId);
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            log.error("녹화 삭제 중 오류 발생", e);
            throw new RuntimeException("녹화를 삭제할 수 없습니다", e);
        }
    }
}