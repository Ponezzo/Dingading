package com.mickey.dinggading.domain.livehouse.service;

import io.openvidu.java.client.Connection;
import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.openvidu.java.client.Session;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 참가자 관리 관련 메서드들
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ParticipantManager {

    private final OpenVidu openVidu;

    /**
     * 강퇴 기능 - 세션에서 참가자 연결 강제 종료
     *
     * @param session          세션 객체
     * @param targetConnection 대상 연결
     */
    public void kickParticipant(Session session, Connection targetConnection) {
        try {
            session.forceDisconnect(targetConnection);
            log.info("참가자 강퇴: 세션={}, 연결ID={}", session.getSessionId(), targetConnection.getConnectionId());
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            log.error("참가자 강퇴 중 오류 발생", e);
            throw new RuntimeException("참가자를 강퇴할 수 없습니다", e);
        }
    }

    /**
     * 참가자 ID로 강퇴
     *
     * @param sessionId    세션 ID
     * @param connectionId 연결 ID
     */
    public void kickParticipantById(String sessionId, String connectionId) {
        try {
            Session session = openVidu.getActiveSession(sessionId);
            if (session != null) {
                session.fetch();
                Connection connection = session.getConnection(connectionId);
                if (connection != null) {
                    session.forceDisconnect(connection);
                    log.info("참가자 ID로 강퇴: 세션={}, 연결ID={}", sessionId, connectionId);
                }
            }
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            log.error("참가자 ID로 강퇴 중 오류 발생", e);
            throw new RuntimeException("참가자를 강퇴할 수 없습니다", e);
        }
    }

    /**
     * 세션의 모든 참가자 목록 가져오기
     *
     * @param sessionId 세션 ID
     * @return 참가자 연결 목록
     */
    public List<Connection> getSessionParticipants(String sessionId) {
        try {
            Session session = openVidu.getActiveSession(sessionId);
            if (session != null) {
                session.fetch();
                return session.getConnections();
            }
            return null;
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            log.error("세션 참가자 목록 조회 중 오류 발생", e);
            throw new RuntimeException("세션 참가자 목록을 가져올 수 없습니다", e);
        }
    }

    /**
     * 참가자 연결 정보 가져오기
     *
     * @param sessionId    세션 ID
     * @param connectionId 연결 ID
     * @return 참가자 연결 정보
     */
    public Connection getParticipantConnection(String sessionId, String connectionId) {
        try {
            Session session = openVidu.getActiveSession(sessionId);
            if (session != null) {
                session.fetch();
                return session.getConnection(connectionId);
            }
            return null;
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            log.error("참가자 연결 정보 조회 중 오류 발생", e);
            throw new RuntimeException("참가자 연결 정보를 가져올 수 없습니다", e);
        }
    }
}