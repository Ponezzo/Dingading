package com.mickey.dinggading.domain.chatMongo.converter;

import com.mickey.dinggading.domain.chatMongo.model.entity.ChatMessageMongo;
import com.mickey.dinggading.domain.chatMongo.model.entity.ChatRoomMongo;
import com.mickey.dinggading.model.ChatMessageDTO;
import com.mickey.dinggading.model.ChatRoomDTO;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatRoomMongoConverter {

    public ChatRoomDTO toDto(ChatRoomMongo chatroom) {
        return ChatRoomDTO.builder()
                .chatroomId(chatroom.getId())
                .roomType(chatroom.getRoomType())
                .latestChat(chatroom.getLatestChat())
                .latestChatDate(chatroom.getLatestChatDate() != null ?
                        chatroom.getLatestChatDate() : null)
                .participantCount(chatroom.getParticipants().size())
                .title(chatroom.getSettings().get(0).getTitle())
                .build();
    }

    public ChatMessageDTO toMessageDto(ChatMessageMongo chatMessage, UUID memberId) {
        if (chatMessage == null) {
            return null;
        }

        return ChatMessageDTO.builder()
                .chatRoomId(chatMessage.getChatRoomId())
                .senderId(memberId)
                .senderNickname(chatMessage.getMemberNickname())
                .senderProfileUrl(chatMessage.getMemberProfileUrl())
                .message(chatMessage.getMessage())
                .messageType(chatMessage.getMessageType())
                .readCount(chatMessage.getReadCount())
                .writedAt(chatMessage.getCreatedAt())
                .build();
    }
}
