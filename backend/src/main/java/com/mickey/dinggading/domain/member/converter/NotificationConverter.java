package com.mickey.dinggading.domain.member.converter;

import com.mickey.dinggading.domain.member.model.entity.Notification;
import com.mickey.dinggading.model.NotificationDTO;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@Builder
@Component
@NoArgsConstructor
public class NotificationConverter {

    public NotificationDTO fromEntity(Notification entity) {
        return NotificationDTO.builder()
                .tierAndInstrument(entity.getTierAndInstrument())
                .notificationId(entity.getNotificationId())
                .chatRoomId(entity.getChatRoomId())
                .attemptId(entity.getAttemptId())
                .isSuccess(entity.getIsSuccess())
                .senderId(entity.getSender().getMemberId())
                .receiverId(entity.getReceiver().getMemberId())
                .senderNickname(entity.getSender().getNickname())
                .senderProfileUrl(entity.getSender().getProfileImgUrl())
                .message(entity.getMessage())
                .type(entity.getType())
                .readOrNot(entity.getReadOrNot())
                .createdAt(LocalDateTime.now())
                .build();
    }
}
