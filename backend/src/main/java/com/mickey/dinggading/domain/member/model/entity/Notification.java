package com.mickey.dinggading.domain.member.model.entity;

import com.mickey.dinggading.base.BaseEntity;
import com.mickey.dinggading.model.NotificationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "Notification")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Member sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Member receiver;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "read_or_not", nullable = false)
    private Boolean readOrNot;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    @Comment("알림 타입")
    private NotificationType type;

    @Column(name = "chat_room_id")
    @Comment("채팅방 ID")
    private String chatRoomId;

    @Column(name = "attempt_id")
    private Long attemptId;

    @Column(name = "is_success")
    private Boolean isSuccess;

    @Column(name = "tier_and_instrument")
    private String tierAndInstrument;

    public Notification(String chatRoomId, String message, Member sender, Member receiver, NotificationType type,
                        boolean readOrNot) {
        this.chatRoomId = chatRoomId;
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.type = type;
        this.readOrNot = readOrNot;
    }

    public Notification(Member ranker, String message, NotificationType type, boolean readOrNot, Long attemptId,
                        boolean isSuccess, String tierAndInstrument) {
        this.message = message;
        this.type = type;
        this.sender = ranker;
        this.receiver = ranker;
        this.readOrNot = readOrNot;
        this.attemptId = attemptId;
        this.isSuccess = isSuccess;
        this.tierAndInstrument = tierAndInstrument;
    }

    // Update methods
    public void markAsRead() {
        this.readOrNot = true;
    }
}