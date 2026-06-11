package com.mickey.dinggading.domain.member.controller;

import com.mickey.dinggading.api.NotificationApi;
import com.mickey.dinggading.domain.member.service.NotificationService;
import com.mickey.dinggading.model.NotificationDTO;
import com.mickey.dinggading.util.SecurityUtil;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class NotificationController implements NotificationApi {

    private final NotificationService notificationService;
    private final SecurityUtil securityUtil;

    @GetMapping(value = "/api/notifications/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe() {
        // 토큰에서 사용자 ID 추출
        UUID memberId = securityUtil.getCurrentMemberId();
        return notificationService.subscribe(memberId);
    }

    /**
     * GET /api/notifications : 알림 목록 조회 현재 로그인한 사용자의 알림 목록을 조회합니다.
     *
     * @param pageable 페이징 정보
     * @return 알림 목록 (status code 200)
     */
    @Override
    @GetMapping("/api/notifications")
    public ResponseEntity<?> getNotifications(Boolean unreadOnly, Pageable pageable) {
        UUID memberId = securityUtil.getCurrentMemberId();
        Page<NotificationDTO> notifications = notificationService.getNotifications(memberId, pageable, unreadOnly);
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/api/notifications/{notificationId}/read")
    public NotificationDTO markAsRead(@PathVariable Long notificationId) {
        UUID memberId = securityUtil.getCurrentMemberId();
        return notificationService.markAsRead(notificationId, memberId);
    }

    @PutMapping("/api/notifications/read-all")
    public int markAllAsRead() {
        UUID memberId = securityUtil.getCurrentMemberId();
        return notificationService.markAllAsRead(memberId);
    }
}