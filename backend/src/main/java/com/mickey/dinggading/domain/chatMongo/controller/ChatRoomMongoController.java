package com.mickey.dinggading.domain.chatMongo.controller;

import com.mickey.dinggading.api.ChatRoomApi;
import com.mickey.dinggading.domain.chatMongo.service.ChatRoomMongoService;
import com.mickey.dinggading.model.ChatMessageDTO;
import com.mickey.dinggading.model.ChatRoomDTO;
import com.mickey.dinggading.model.SendMessageRequest;
import com.mickey.dinggading.util.SecurityUtil;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatRoomMongoController implements ChatRoomApi {

    private final ChatRoomMongoService chatRoomMongoService;
    private final SecurityUtil securityUtil;

    /**
     * GET /ws/chat/{roomId} : WebSocket 연결 채팅방 WebSocket 연결을 수립합니다. 메시지 전송, 수신, 타이핑 알림 등 실시간 통신에 사용됩니다.
     *
     * @param roomId 채팅방 ID (required)
     * @return Void WebSocket 연결이 수립되었습니다. (status code 101)
     */
    @Override
    public ResponseEntity<?> connectWebSocket(String roomId) {
        log.info("WebSocket 연결 요청. 채팅방 ID: {}", roomId);

        // 로그인 한 사용자만 이용 가능
        UUID currentUserId = securityUtil.getCurrentMemberId();

        // 채팅방 존재 여부 확인
        chatRoomMongoService.validateChatroom(roomId);

        // WebSocket은 Spring WebSocket 설정에 의해 실제 연결이 수립되므로,
        // 이 메서드는 주로 문서화 목적으로 존재함
        // 실제 WebSocket 연결은 WebSocketConfig에서 처리

        return ResponseEntity.ok().build();
    }

    /**
     * GET /chatrooms : 채팅방 목록 조회 사용자의 모든 채팅방 목록을 조회합니다.
     *
     * @return PageChatRoomDTO 채팅방 목록 (status code 200)
     */
    @Override
    public ResponseEntity<?> getChatRooms() {
        UUID currentUserId = securityUtil.getCurrentMemberId();
        List<ChatRoomDTO> chatRooms = chatRoomMongoService.getChatRoomsForUser(currentUserId);
        return ResponseEntity.ok().body(chatRooms);
    }

    /**
     * POST /chatrooms/personal/{memberId} : 1:1 DM 생성/조회 특정 회원과의 1:1 DM을 생성하거나 기존 채팅방을 조회합니다.
     *
     * @param memberId 채팅할 상대방 회원 ID (required)
     * @return ChatRoomDTO 채팅방 정보 (status code 200), 채팅방 정보를 담은 응답 or 잘못된 요청입니다. (status code 400) or 요청한 리소스를 찾을 수
     * 없습니다. (status code 404)
     */
    @Override
    public ResponseEntity<?> createPersonalChatRoom(@PathVariable UUID memberId) {
        log.info("1:1 DM 채팅방 생성/조회 요청. 상대방 ID: {}", memberId);

        UUID currentUserId = securityUtil.getCurrentMemberId();

        // 채팅방 생성 또는 조회
        ChatRoomDTO chatRoomDTO = chatRoomMongoService.getOrCreatePersonalChatRoom(currentUserId, memberId);

        return ResponseEntity.ok(chatRoomDTO);
    }

    /**
     * POST /chatrooms/{roomId}/messages : 메시지 전송 채팅방에 메시지를 전송합니다.
     *
     * @param roomId             채팅방 ID (required)
     * @param sendMessageRequest (required)
     * @return ChatMessageDTO 채팅 메시지 정보 (status code 201) or 잘못된 요청입니다. (status code 400) or 접근 권한이 없습니다. (status code
     * 403) or 요청한 리소스를 찾을 수 없습니다. (status code 404)
     */
    @Override
    public ResponseEntity<?> sendMessage(String roomId, SendMessageRequest sendMessageRequest) {
        log.info("메시지 전송 요청. 채팅방 ID: {}", roomId);

        // 현재 로그인한 사용자 ID 획득
        UUID currentUserId = securityUtil.getCurrentMemberId();

        // 메시지 전송 서비스 호출
        ChatMessageDTO chatMessageDTO = chatRoomMongoService.sendMessage(roomId, currentUserId, sendMessageRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(chatMessageDTO);
    }

    /**
     * GET /chatrooms/{roomId} : 채팅방 정보 조회 특정 채팅방의 정보를 조회합니다.
     *
     * @param roomId 채팅방 ID (required)
     * @return ChatRoomDTO 채팅방 정보 (status code 200) or 접근 권한이 없습니다. (status code 403) or 요청한 리소스를 찾을 수 없습니다. (status
     * code 404)
     */
    @Override
    public ResponseEntity<?> getChatRoom(String roomId) {
        UUID currentUserId = securityUtil.getCurrentMemberId();
        ChatRoomDTO chatRoomDTO = chatRoomMongoService.getChatRoom(roomId, currentUserId);
        return ResponseEntity.ok(chatRoomDTO);
    }

    /**
     * GET /chatrooms/{roomId}/messages : 메시지 히스토리 조회 채팅방의 메시지 히스토리를 조회합니다.
     *
     * @param roomId 채팅방 ID (required)
     * @return ChatMessageDTO 채팅 메시지 목록 (status code 200) or 접근 권한이 없습니다. (status code 403) or 요청한 리소스를 찾을 수 없습니다.
     * (status code 404)
     */
    @Override
    public ResponseEntity<?> getChatMessages(String roomId) {
        UUID currentUserId = securityUtil.getCurrentMemberId();
        // 100개 메시지 조회
        Pageable pageable = PageRequest.of(0, 100);
        Page<ChatMessageDTO> messages = chatRoomMongoService.getChatMessages(roomId,
                currentUserId, pageable);
        log.info("채팅방 메시지 히스토리 조회 성공. 채팅방 ID: {}, 메시지 수: {}", roomId, messages.getContent().size());
        log.info(messages.getContent().toString());

        return ResponseEntity.ok(messages);
    }

}
