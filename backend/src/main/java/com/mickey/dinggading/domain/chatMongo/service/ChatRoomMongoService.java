package com.mickey.dinggading.domain.chatMongo.service;

import com.mickey.dinggading.model.ChatMessageDTO;
import com.mickey.dinggading.model.ChatRoomDTO;
import com.mickey.dinggading.model.SendMessageRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatRoomMongoService {
    void validateChatroom(String roomId);

    ChatMessageDTO sendMessage(String roomId, UUID currentUserId, SendMessageRequest sendMessageRequest);

    ChatRoomDTO getChatRoom(String roomId, UUID currentUserId);

    Page<ChatMessageDTO> getChatMessages(String roomId, UUID currentUserId, Pageable pageable);

    List<ChatRoomDTO> getChatRoomsForUser(UUID currentUserId);

    ChatRoomDTO getOrCreatePersonalChatRoom(UUID currentUserId, UUID memberId);
}
