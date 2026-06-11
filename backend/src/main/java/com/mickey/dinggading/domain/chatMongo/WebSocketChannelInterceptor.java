package com.mickey.dinggading.domain.chatMongo;

import com.mickey.dinggading.base.status.ErrorStatus;
import com.mickey.dinggading.domain.chatMongo.repository.MongoChatRoomRepository;
import com.mickey.dinggading.exception.ExceptionHandler;
import com.mickey.dinggading.util.JWTUtil;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketChannelInterceptor implements ChannelInterceptor {

    private final JWTUtil jwtUtil;
    private final MongoChatRoomRepository mongoChatRoomRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String destination = accessor.getDestination();

            // /topic/chat/{roomId} 형식에서 roomId 추출
            if (destination != null && destination.startsWith("/topic/chatrooms/")) {
                String roomId = destination.substring("/topic/chatrooms/".length());

                // 토큰에서 사용자 ID 추출
                String authHeader = accessor.getFirstNativeHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    UUID memberId = jwtUtil.getMemberId(token);

                    // 해당 사용자가 채팅방 참여자인지 확인
                    isParticipant(roomId, memberId);
                }
            }
        }

        return message;
    }

    public boolean isParticipant(String roomId, UUID memberId) {
        if (!mongoChatRoomRepository.existsByIdAndParticipantsMemberId(roomId, memberId.toString())) {
            throw new ExceptionHandler(ErrorStatus.CHATROOM_ACCESS_DENIED);
        }
        return true;
    }
}