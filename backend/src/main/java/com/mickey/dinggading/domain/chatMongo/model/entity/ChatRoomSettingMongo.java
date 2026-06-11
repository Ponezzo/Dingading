package com.mickey.dinggading.domain.chatMongo.model.entity;

import com.mickey.dinggading.domain.member.model.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chat_room_setting")
public class ChatRoomSettingMongo {

    @Id
    private String chatRoomSettingId;

    private String chatRoomId;

    // 채팅룸 이용 당사자
    private String memberId;
    private Boolean alert;
    private Integer unreadChatCount;

    // 사용자의 상대방 id 혹은 단체방의 이름
    private String title;
    // 사용자의 상대방 프로필 혹은 지정한 사진
    private String chatProfileUrl;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 방 공지사항
    @Column(name = "pinned_chat_room_order")
    private Integer pinnedChatRoomOrder;

    public ChatRoomSettingMongo(ChatRoomMongo chatRoom, Member member, String title, String chatProfileUrl) {
        this.chatRoomId = chatRoom.getId();
        this.memberId = member.getMemberId().toString();
        this.alert = true;
        this.unreadChatCount = 0;
        this.title = title;
        this.chatProfileUrl = chatProfileUrl;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateAlert(boolean alert) {
        this.alert = alert;
        this.updatedAt = LocalDateTime.now();
    }

    public void updatePinnedChatRoomOrder(Integer pinnedChatRoomOrder) {
        this.pinnedChatRoomOrder = pinnedChatRoomOrder;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateUnreadCount(int count) {
        this.unreadChatCount = count;
    }

    public void incrementUnreadCount() {
        this.unreadChatCount = this.unreadChatCount + 1;
    }

    public void resetUnreadCount() {
        this.unreadChatCount = 0;
    }

    public void setChatRoom(ChatRoomMongo chatRoom) {
        this.chatRoomId = chatRoom.getId();
    }
}