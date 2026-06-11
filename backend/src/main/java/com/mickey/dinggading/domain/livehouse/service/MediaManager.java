package com.mickey.dinggading.domain.livehouse.service;

import io.openvidu.java.client.Connection;
import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.openvidu.java.client.Publisher;
import io.openvidu.java.client.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 미디어 스트림 관리 관련 메서드들
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MediaManager {

    private final OpenVidu openVidu;

    /**
     * 특정 참가자의 미디어 스트림 게시 중지 (음소거)
     *
     * @param session         세션 객체
     * @param targetPublisher 대상 게시자
     */
    public void muteParticipant(Session session, Publisher targetPublisher) {
        try {
            session.forceUnpublish(targetPublisher);
            log.info("참가자 음소거: 세션={}, 스트림ID={}", session.getSessionId(), targetPublisher.getStreamId());
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            log.error("참가자 음소거 중 오류 발생", e);
            throw new RuntimeException("참가자 미디어를 음소거할 수 없습니다", e);
        }
    }

    /**
     * 스트림 ID로 참가자 음소거
     *
     * @param sessionId 세션 ID
     * @param streamId  스트림 ID
     */
    public void muteParticipantByStreamId(String sessionId, String streamId) {
        try {
            Session session = openVidu.getActiveSession(sessionId);
            if (session != null) {
                session.fetch();
                for (Connection connection : session.getConnections()) {
                    for (Publisher publisher : connection.getPublishers()) {
                        if (publisher.getStreamId().equals(streamId)) {
                            session.forceUnpublish(publisher);
                            log.info("스트림 ID로 참가자 음소거: 세션={}, 스트림ID={}", sessionId, streamId);
                            return;
                        }
                    }
                }
            }
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            log.error("스트림 ID로 참가자 음소거 중 오류 발생", e);
            throw new RuntimeException("참가자 미디어를 음소거할 수 없습니다", e);
        }
    }

    /**
     * 특정 참가자를 제외한 모든 참가자 음소거
     *
     * @param sessionId           세션 ID
     * @param excludeConnectionId 제외할 참가자 연결 ID
     */
    public void muteAllExcept(String sessionId, String excludeConnectionId) {
        try {
            Session session = openVidu.getActiveSession(sessionId);
            if (session != null) {
                session.fetch();
                for (Connection connection : session.getConnections()) {
                    if (!connection.getConnectionId().equals(excludeConnectionId)) {
                        for (Publisher publisher : connection.getPublishers()) {
                            session.forceUnpublish(publisher);
                        }
                    }
                }
                log.info("특정 참가자 제외 전체 음소거: 세션={}, 제외ID={}", sessionId, excludeConnectionId);
            }
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            log.error("전체 음소거 중 오류 발생", e);
            throw new RuntimeException("전체 참가자를 음소거할 수 없습니다", e);
        }
    }
}