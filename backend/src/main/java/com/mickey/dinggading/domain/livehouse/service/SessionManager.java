package com.mickey.dinggading.domain.livehouse.service;

import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.openvidu.java.client.Session;
import io.openvidu.java.client.SessionProperties;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 세션 관리 관련 메서드들
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SessionManager {

    private final OpenVidu openVidu;

    /**
     * 새 세션 생성
     *
     * @param customSessionId 사용자 지정 세션 ID (선택사항)
     * @param properties      세션 속성
     * @return 생성된 세션
     */
    public Session createSession(String customSessionId, SessionProperties properties) {
        try {
            if (customSessionId != null && !customSessionId.isEmpty()) {
                properties = new SessionProperties.Builder()
                        .customSessionId(customSessionId)
                        .build();
            }
            return openVidu.createSession(properties);
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            log.error("세션 생성 중 오류 발생", e);
            throw new RuntimeException("세션을 생성할 수 없습니다", e);
        }
    }

    /**
     * 세션 정보 업데이트
     *
     * @param sessionId 세션 ID
     * @return 업데이트된 세션
     */
    public Session getSession(String sessionId) {
        try {
            openVidu.fetch();
            return openVidu.getActiveSession(sessionId);
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            log.error("세션 정보 조회 중 오류 발생", e);
            throw new RuntimeException("세션 정보를 가져올 수 없습니다", e);
        }
    }

    /**
     * 모든 활성 세션 목록 가져오기
     *
     * @return 활성 세션 목록
     */
    public List<Session> getActiveSessions() {
        try {
            openVidu.fetch();
            return openVidu.getActiveSessions();
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            log.error("활성 세션 목록 조회 중 오류 발생", e);
            throw new RuntimeException("활성 세션 목록을 가져올 수 없습니다", e);
        }
    }

    /**
     * 세션 종료
     *
     * @param sessionId 종료할 세션 ID
     */
    public void closeSession(String sessionId) {
        try {
            Session session = openVidu.getActiveSession(sessionId);
            if (session != null) {
                session.close();
                log.info("세션 종료: {}", sessionId);
            }
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            log.error("세션 종료 중 오류 발생", e);
            throw new RuntimeException("세션을 종료할 수 없습니다", e);
        }
    }
}