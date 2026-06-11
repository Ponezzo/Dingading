package com.mickey.dinggading.domain.chatMongo.model.entity;

import com.mickey.dinggading.model.ChatRoomType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chat_room")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomMongo {
    @Id
    private String id;

    private ChatRoomType roomType;
    private String latestChat;
    private LocalDateTime latestChatDate;

    private List<ChatRoomParticipantMongo> participants = new ArrayList<>();
    private List<ChatMessageMongo> messages = new ArrayList<>();
    private List<ChatRoomSettingMongo> settings = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ChatRoomMongo(ChatRoomType chatRoomType) {
        this.roomType = chatRoomType;
    }

    public void updateLatestChat(String message, LocalDate messageDate) {
        this.latestChat = message;
        this.latestChatDate = messageDate.atStartOfDay();
    }

    public void addParticipant(ChatRoomParticipantMongo currentUserParticipant) {
        this.participants.add(currentUserParticipant);
        currentUserParticipant.setChatRoom(this);
    }

    public void addRoomSetting(ChatRoomSettingMongo chatRoomSetting) {
        this.settings.add(chatRoomSetting);
        chatRoomSetting.setChatRoom(this);
    }

    public void removeParticipant(ChatRoomParticipantMongo participant) {
        this.participants.remove(participant);
        participant.setChatRoom(null);
    }

}