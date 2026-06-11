package com.mickey.dinggading.domain.livehouse.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mickey.dinggading.domain.livehouse.entity.Livehouse;
import com.mickey.dinggading.domain.livehouse.repository.LivehouseRepository;
import com.mickey.dinggading.domain.livehouse.repository.ParticipantRepository;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WebhookController {

    private final ParticipantRepository participantRepository;
    private final LivehouseRepository livehouseRepository;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody WebhookEvent event) {
        log.info("Received webhook event: {}, sessionId: {}, connectionId: {}",
                event.getEvent(), event.getSessionId(), event.getConnectionId());

        try {
            switch (event.getEvent()) {
                case "sessionDestroyed":
                    handleSessionDestroyed(event);
                    break;
                case "participantLeft":
                    handleParticipantLeft(event);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok("Event received");
    }

    private void handleParticipantLeft(WebhookEvent event) {
        log.info("Participant left: {}, connection: {}",
                event.getParticipantId(), event.getConnectionId());

        // 참가자가 세션을 떠날 때 DB에서 참가자 정보 삭제
        if (event.getConnectionId() != null) {
            participantRepository.findByConnectionId(event.getConnectionId())
                    .ifPresentOrElse(
                            participant -> {
                                log.info("Deleting participant with connectionId: {}", event.getConnectionId());

                                // 방장이 나갔는지 확인
                                if (participant.isHost()) {
                                    log.info("Host left the session, closing livehouse");
                                    Livehouse livehouse = participant.getLivehouse();
                                    livehouse.close();

                                    // 라이브하우스의 모든 참가자 삭제
                                    participantRepository.deleteByLivehouse(livehouse);

                                    livehouseRepository.delete(livehouse);
                                } else {
                                    // 일반 참가자만 삭제
                                    participantRepository.delete(participant);
                                }
                            },
                            () -> log.warn("Participant left but not found in DB: {}", event.getConnectionId())
                    );
        }
    }

    private void handleSessionDestroyed(WebhookEvent event) {
        log.info("Session destroyed: {}", event.getSessionId());

        // 세션이 종료되었을 때 라이브하우스 상태 및 참가자 정보 정리
        livehouseRepository.findBySessionId(event.getSessionId())
                .ifPresent(livehouse -> {
                    log.info("Closing livehouse with sessionId: {}", event.getSessionId());

                    // 해당 라이브하우스의 모든 참가자 삭제
                    List<String> connectionIds = event.getReason().getConnections().stream()
                            .map(Connection::getConnectionId)
                            .toList();

                    log.info("Deleting participants with connectionIds: {}", connectionIds);
                    for (String connectionId : connectionIds) {
                        participantRepository.findByConnectionId(connectionId)
                                .ifPresent(participantRepository::delete);
                    }

                    // 라이브하우스 삭제
                    livehouseRepository.delete(livehouse);
                });
    }
}

// 웹훅 이벤트를 받기 위한 확장된 DTO 클래스
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
class WebhookEvent {
    private String event;              // 이벤트 타입
    private String sessionId;          // 세션 ID
    private String connectionId;       // 연결 ID
    private String participantId;      // 참가자 ID (OpenVidu 내부 식별자)
    private Long timestamp;            // 타임스탬프
    private String streamId;           // 스트림 ID
    private String recordingId;        // 녹화 ID
    private String filterType;         // 필터 타입
    private String signalType;         // 시그널 타입
    private WebhookReason reason;      // 이벤트 추가 정보
}

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
class WebhookReason {
    private String reason;                  // 이유 (disconnect, networkDisconnect 등)
    private String status;                  // 상태 (녹화 상태 등)
    private List<Connection> connections;   // 연결 목록 (sessionDestroyed 이벤트에서 사용)
}

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
class Connection {
    private String connectionId;            // 연결 ID
    private String clientData;              // 클라이언트 데이터
    private String serverData;              // 서버 데이터
}

/*
로컬 테스트 코드
# participantLeft 이벤트 시뮬레이션
curl -X POST http://localhost:8080/webhook \
  -H "Content-Type: application/json" \
  -d '{"event":"participantLeft","sessionId":"session_123","connectionId":"connection_456"}'

# sessionDestroyed 이벤트 시뮬레이션
curl -X POST http://localhost:8080/webhook \
  -H "Content-Type: application/json" \
  -d '{"event":"sessionDestroyed","sessionId":"session_123","reason":{"connections":[{"connectionId":"connection_456"}]}}'
*/