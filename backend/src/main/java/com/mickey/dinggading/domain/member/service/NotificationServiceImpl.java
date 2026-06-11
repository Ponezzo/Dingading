package com.mickey.dinggading.domain.member.service;

import com.mickey.dinggading.base.status.ErrorStatus;
import com.mickey.dinggading.domain.member.converter.NotificationConverter;
import com.mickey.dinggading.domain.member.model.entity.Member;
import com.mickey.dinggading.domain.member.model.entity.Notification;
import com.mickey.dinggading.domain.member.repository.MemberRepository;
import com.mickey.dinggading.domain.member.repository.NotificationRepository;
import com.mickey.dinggading.domain.membermatching.repository.AttemptRepository;
import com.mickey.dinggading.domain.memberrank.model.Attempt;
import com.mickey.dinggading.domain.memberrank.model.MemberRank;
import com.mickey.dinggading.domain.memberrank.repository.MemberRankRepository;
import com.mickey.dinggading.exception.ExceptionHandler;
import com.mickey.dinggading.infra.rabbitmq.dto.MessageDTO;
import com.mickey.dinggading.model.NotificationDTO;
import com.mickey.dinggading.model.NotificationType;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final long SSE_TIMEOUT = 60 * 60 * 1000L * 2; // 2시간
    private final Map<UUID, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationConverter notificationConverter;
    private final MemberRankRepository memberRankRepository;
    // 사용자별 SSE 연결 시간을 저장
    private final Map<UUID, LocalDateTime> connectionStartTimes = new ConcurrentHashMap<>();
    private final AttemptRepository attemptRepository;

    @Override
    public SseEmitter subscribe(UUID memberId) {
        log.info("SSE 구독 시작: 사용자 {}", memberId);

        // 기존 emitter 가 있다면 삭제
        if (emitters.containsKey(memberId)) {
            SseEmitter oldEmitter = emitters.get(memberId);
            oldEmitter.complete();
            emitters.remove(memberId);
        }

        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);

        emitter.onCompletion(() -> {
            log.debug("SSE 연결 완료: 사용자 {}", memberId);
            emitters.remove(memberId);
        });

        emitter.onTimeout(() -> {
            log.debug("SSE 연결 시간 초과: 사용자 {}", memberId);
            emitter.complete();
            emitters.remove(memberId);
        });

        emitter.onError((ex) -> {
            log.error("SSE 연결 에러: 사용자 {}, 에러: {}", memberId, ex.getMessage());
            emitter.complete();
            emitters.remove(memberId);
        });

        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("Connected to notification stream"));
        } catch (IOException e) {
            log.error("SSE 초기 메세지 전송 실패: 사용자 {}, 에러 : {}", memberId, e.getMessage());
            emitter.complete();
            return emitter;
        }

        // emitter 저장
        emitters.put(memberId, emitter);
        log.info("SSE 구독 완료: 사용자 {}, 현재 연결 수: {}", memberId, emitters.size());

        connectionStartTimes.put(memberId, LocalDateTime.now());

        return emitter;
    }

    @Override
    public NotificationDTO createChatMessageNotification(String chatRoomId, UUID senderId, UUID receiverId,
                                                         String message, LocalDateTime time) {

        // 발신자가 자신인 경우 알림 생성하지 않음
        if (senderId.toString().equals(receiverId)) {
            log.debug("자신에게 보낸 메시지는 알림을 생성하지 않습니다.");
            return null;
        }
        log.debug("새 메시지 알림 생성 시작: 채팅방 {}, 메시지 {}, 발신자 {}, 수신자 {}", chatRoomId, message, senderId, receiverId);

        Member sender = getMember(senderId);
        Member receiver = getMember(receiverId);

        // 알림 메시지 생성 (긴 메시지는 짧게 요약)
        String notificationContent = message;
        if (message.length() > 30) {
            notificationContent = message.substring(0, 27) + "...";
        }

        // 알림 엔티티 생성 및 저장
        Notification notification = new Notification(chatRoomId, notificationContent, sender, receiver,
                NotificationType.CHAT, false);

        Notification savedNotification = notificationRepository.save(notification);
        log.info("새 메시지 알림 생성 완료: ID {}", savedNotification.getNotificationId());

        // 실시간 알림 발송
        sendNotification(receiverId, notificationConverter.fromEntity(savedNotification));

        return notificationConverter.fromEntity(savedNotification);
    }

    @Override
    public void createTierMessageNotification(UUID rankerId, MessageDTO message) {

        log.debug("새 메시지 알림 생성 시작: 메시지 {}, 랭커{}", message, rankerId);

        Member member = getMember(rankerId);

        String notificationContent = "티어 분석이 완료되었습니다. 확인해보세요!";

        Attempt attempt = attemptRepository.findById(message.getAttemptId())
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.ATTEMPT_NOT_FOUND));

        MemberRank memberRank = memberRankRepository.findByMemberAndInstrument(member,
                        attempt.getSongByInstrument().getInstrument())
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.MEMBER_RANK_NOT_FOUND));

        String tierAndInstrument = memberRank.getTier() + "" + attempt.getSongByInstrument().getInstrument();

        // 알림 엔티티 생성 및 저장
        Notification notification = new Notification(member, notificationContent, NotificationType.RANK, false,
                message.getAttemptId(), attempt.isSuccess(), tierAndInstrument);

        Notification savedNotification = notificationRepository.save(notification);
        log.info("tierAndInstrument = {}", tierAndInstrument);
        log.info("새 메시지 알림 생성 완료: ID {}", savedNotification.getNotificationId());

        // 실시간 알림 발송
        sendNotification(rankerId, notificationConverter.fromEntity(savedNotification));

        notificationConverter.fromEntity(savedNotification);
    }

    private Member getMember(UUID memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.MEMBER_NOT_FOUND));
    }

    private void sendNotification(UUID memberId, NotificationDTO notification) {
        log.debug("실시간 알림 전송 시도: 사용자 {}, 알림 ID {}", memberId, notification.getNotificationId());

        SseEmitter emitter = emitters.get(memberId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(notification));
                log.debug("실시간 알림 전송 성공: 사용자 {}", memberId);
            } catch (IOException e) {
                log.error("실시간 알림 전송 실패: 사용자 {}, 에러: {}", memberId, e.getMessage());
                emitter.complete();
                emitters.remove(memberId);
            }
        } else {
            log.debug("실시간 알림 전송 실패: 사용자 {}의 연결된 SSE가 없음", memberId);
        }
    }

    @Override
    public NotificationDTO markAsRead(Long notificationId, UUID memberId) {
        log.debug("알림 읽음 처리: 알림 ID {}, 사용자 {}", notificationId, memberId);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.NOTIFICATION_NOT_FOUND));

        // 권한 확인
        if (!notification.getReceiver().getMemberId().equals(memberId)) {
            throw new ExceptionHandler(ErrorStatus._FORBIDDEN);
        }

        // 읽음 처리
        notification.markAsRead();
        Notification savedNotification = notificationRepository.save(notification);

        log.info("알림 읽음 처리 완료: ID {}", savedNotification.getNotificationId());
        return notificationConverter.fromEntity(savedNotification);
    }

    @Override
    public int markAllAsRead(UUID memberId) {
        log.debug("모든 알림 읽음 처리: 사용자 {}", memberId);

        int count = notificationRepository.markAllAsReadByReceiverId(memberId);

        log.info("모든 알림 읽음 처리 완료: 사용자 {}, 처리된 알림 수 {}", memberId, count);
        return count;
    }

    @Override
    public int markChatRoomNotificationsAsRead(String chatRoomId, UUID memberId) {
        log.debug("채팅방 알림 읽음 처리: 채팅방 {}, 사용자 {}", chatRoomId, memberId);

        int count = notificationRepository.markAllAsReadByChatRoomIdAndReceiverId(chatRoomId, memberId);

        log.info("채팅방 알림 읽음 처리 완료: 채팅방 {}, 사용자 {}, 처리된 알림 수 {}", chatRoomId, memberId, count);
        return count;
    }

    @Override
    public Page<NotificationDTO> getNotifications(UUID memberId, Pageable pageable, Boolean unreadOnly) {
        log.debug("알림 목록 조회: 사용자 {}, 읽지 않은 알림만: {}", memberId, unreadOnly);

        Member receiver = getMember(memberId);
        Page<Notification> notifications;

        if (unreadOnly != null && unreadOnly) {
            // 읽지 않은 알림만 조회
            notifications = notificationRepository.findByReceiverAndReadStatus(receiver, false, pageable);
        } else {
            // 모든 알림 조회
            notifications = notificationRepository.findByReceiver(receiver, pageable);
        }

        return notifications.map(notificationConverter::fromEntity);
    }

    // 사용자가 현재 우리 사이트에 접속 중인지 체크해서 SSE 연결 유지 혹은 제거
    @Scheduled(fixedRate = 900000) // 15분마다 실행
    public void sendHeartbeat() {
        emitters.forEach((memberId, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("heartbeat")
                        .data("ping"));
                log.debug("하트비트 전송 성공: 사용자 {}", memberId);
            } catch (IOException e) {
                log.error("하트비트 전송 실패, 연결 종료: 사용자 {}", memberId);
                emitter.complete();
                emitters.remove(memberId);
            }
        });
    }
}
