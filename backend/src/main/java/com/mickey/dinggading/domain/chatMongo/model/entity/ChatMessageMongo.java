package com.mickey.dinggading.domain.chatMongo.model.entity;

import com.mickey.dinggading.model.MessageType;
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
@Document(collection = "chat_message")
public class ChatMessageMongo {

    @Id
    private String id;

    private String chatRoomId;
    private String memberId;
    private String memberNickname;
    private String memberProfileUrl;

    private String message;
    private MessageType messageType;
    private Integer readCount;

//    @Builder.Default
//    private List<ChatMedia> mediaList = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ChatMessageMongo(String chatRoomId, String memberId, String memberNickname, String memberProfileUrl,
                            String message, MessageType messageType) {
        this.chatRoomId = chatRoomId;
        this.memberId = memberId;
        this.memberNickname = memberNickname;
        this.memberProfileUrl = memberProfileUrl;
        this.message = message;
        this.messageType = messageType;
        this.readCount = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void incrementReadCount() {
        this.readCount = this.readCount + 1;
    }
}