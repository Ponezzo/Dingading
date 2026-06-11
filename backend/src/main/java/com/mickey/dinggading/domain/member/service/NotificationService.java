package com.mickey.dinggading.domain.member.service;

import com.mickey.dinggading.infra.rabbitmq.dto.MessageDTO;
import com.mickey.dinggading.model.NotificationDTO;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationService {
    SseEmitter subscribe(UUID memberId);

    NotificationDTO createChatMessageNotification(String chatRoomId, UUID memberId, UUID receiverId, String content,
                                                  LocalDateTime time);

    NotificationDTO markAsRead(Long notificationId, UUID memberId);

    int markAllAsRead(UUID memberId);

    int markChatRoomNotificationsAsRead(String chatRoomId, UUID memberId);

    Page<NotificationDTO> getNotifications(UUID memberId, Pageable pageable, Boolean unreadOnly);

    void createTierMessageNotification(UUID rankerId, MessageDTO message);
}
