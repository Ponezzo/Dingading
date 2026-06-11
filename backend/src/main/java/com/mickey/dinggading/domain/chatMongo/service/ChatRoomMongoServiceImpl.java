package com.mickey.dinggading.domain.chatMongo.service;

import com.mickey.dinggading.base.status.ErrorStatus;
import com.mickey.dinggading.domain.chatMongo.converter.ChatRoomMongoConverter;
import com.mickey.dinggading.domain.chatMongo.model.entity.ChatMessageMongo;
import com.mickey.dinggading.domain.chatMongo.model.entity.ChatRoomMongo;
import com.mickey.dinggading.domain.chatMongo.model.entity.ChatRoomParticipantMongo;
import com.mickey.dinggading.domain.chatMongo.model.entity.ChatRoomSettingMongo;
import com.mickey.dinggading.domain.chatMongo.repository.MongoChatMessageRepository;
import com.mickey.dinggading.domain.chatMongo.repository.MongoChatRoomRepository;
import com.mickey.dinggading.domain.chatMongo.repository.MongoChatRoomSettingRepository;
import com.mickey.dinggading.domain.member.model.entity.Member;
import com.mickey.dinggading.domain.member.repository.MemberRepository;
import com.mickey.dinggading.domain.member.service.NotificationService;
import com.mickey.dinggading.exception.ExceptionHandler;
import com.mickey.dinggading.model.ChatMessageDTO;
import com.mickey.dinggading.model.ChatRoomDTO;
import com.mickey.dinggading.model.ChatRoomType;
import com.mickey.dinggading.model.ParticipantRole;
import com.mickey.dinggading.model.SendMessageRequest;
import com.mickey.dinggading.model.StompChatMessageDTO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ChatRoomMongoServiceImpl implements ChatRoomMongoService {

    private final MongoTemplate mongoTemplate;
    private final MemberRepository memberRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRoomMongoConverter chatRoomMongoConverter;
    private final MongoChatRoomRepository mongoChatRoomRepository;
    private final MongoChatMessageRepository mongoChatMessageRepository;
    private final NotificationService notificationService;
    private final MongoChatRoomSettingRepository mongoChatRoomSettingRepository;

    @Override
    public List<ChatRoomDTO> getChatRoomsForUser(UUID currentUserId) {
        List<ChatRoomMongo> chatRooms = mongoChatRoomRepository.findByParticipantsMemberId(currentUserId.toString());
        return chatRooms.stream()
                .map(chatRoomMongoConverter::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ChatRoomDTO getOrCreatePersonalChatRoom(UUID currentUserId, UUID targetUserId) {
        log.debug("1:1 DM 채팅방 생성/조회 시작. 현재 사용자: {}, 대화 상대: {}", currentUserId, targetUserId);

        // 두 사용자 정보 조회
        Member currentUser = getMember(currentUserId);
        Member targetUser = getMember(targetUserId);

        boolean isSelfChat = currentUserId.equals(targetUserId);
        ChatRoomType roomType = isSelfChat ? ChatRoomType.SELF : ChatRoomType.PERSONAL;

        // 기존 채팅방 조회
        Optional<ChatRoomMongo> existingChatroom;
        if (isSelfChat) {
            existingChatroom = mongoChatRoomRepository.findByRoomTypeAndParticipantId(currentUserId.toString());
        } else {
            existingChatroom = mongoChatRoomRepository.findPersonalChatroom(currentUserId.toString(),
                    targetUserId.toString());

            // 순서 바꿔서도 체크! (안전 장치)
            if (!existingChatroom.isPresent()) {
                existingChatroom = mongoChatRoomRepository.findPersonalChatroom(targetUserId.toString(),
                        currentUserId.toString());
            }
        }

        if (existingChatroom.isPresent()) {
            log.debug("기존 채팅방 발견. ID: {}, 타입: {}", existingChatroom.get().getId(), roomType);
            return chatRoomMongoConverter.toDto(existingChatroom.get());
        }

        // 새 채팅방 생성
        log.debug("새 1:1 DM 채팅방 생성 시작");

        // 채팅방 제목 생성
        String chatTitle = isSelfChat ? currentUser.getNickname() : targetUser.getNickname();
        String profileImgUrl = isSelfChat ? currentUser.getProfileImgUrl() : targetUser.getProfileImgUrl();

        // 채팅방 생성
        ChatRoomMongo newChatroom = new ChatRoomMongo(roomType);

        // 참가자 추가
        ChatRoomParticipantMongo currentUserParticipant = new ChatRoomParticipantMongo(newChatroom, currentUser,
                ParticipantRole.ADMIN);
        newChatroom.addParticipant(currentUserParticipant);
        ChatRoomSettingMongo chatRoomSettingMongo = new ChatRoomSettingMongo(newChatroom, currentUser, chatTitle,
                profileImgUrl);
        newChatroom.addRoomSetting(chatRoomSettingMongo);

        if (!isSelfChat) {
            ChatRoomParticipantMongo targetUserParticipant = new ChatRoomParticipantMongo(newChatroom, targetUser,
                    ParticipantRole.ADMIN);
            newChatroom.addParticipant(targetUserParticipant);
            ChatRoomSettingMongo chatRoomSettingMongo2 = new ChatRoomSettingMongo(newChatroom, targetUser,
                    targetUser.getNickname(), targetUser.getProfileImgUrl());
            newChatroom.addRoomSetting(chatRoomSettingMongo2);
        }

        ChatRoomMongo savedChatroom = mongoChatRoomRepository.save(newChatroom);
        log.info("1:1 DM 채팅방 생성 완료. 채팅방 ID: {}, 참가자: {} <-> {}",
                savedChatroom.getId(), currentUserId, targetUserId);

        // 생성된 채팅방 정보 반환
        ChatRoomDTO chatRoomDTO = chatRoomMongoConverter.toDto(savedChatroom);
        chatRoomDTO.setUnreadCount(0); // 새로 생성된 채팅방은 읽지 않은 메시지 없음

        return chatRoomDTO;
    }

    @Override
    public ChatMessageDTO sendMessage(String roomId, UUID currentUserId, SendMessageRequest sendMessageRequest) {

        Member currentUser = getMember(currentUserId);
        ChatRoomMongo chatRoom = getChatRoomMongo(roomId);

        // 참여 권한 확인 (MongoDB에서도 참여자 확인 필요)
        isParticipant(roomId, currentUserId);

        // 메시지 생성 및 저장
        ChatMessageMongo message = new ChatMessageMongo(
                chatRoom.getId(),
                currentUserId.toString(),
                currentUser.getNickname(),
                currentUser.getProfileImgUrl(),
                sendMessageRequest.getMessage(),
                sendMessageRequest.getMessageType()
        );

        ChatMessageMongo savedMessage = mongoChatMessageRepository.save(message);

        // 채팅방 최신 메시지 업데이트
        // MongoDB 는 직접 Update 연산이 필요함
        Update update = new Update();
        update.set("latestChat", message.getMessage());
        update.set("latestChatDate", LocalDateTime.now());

        // 업데이트 할 문서 쿼리 생성
        Query query = new Query(Criteria.where("_id").is(chatRoom.getId()));
        // MongoDB 에 직접 업데이트 요청
        mongoTemplate.updateFirst(query, update, ChatRoomMongo.class);

        // 채팅방 참가자들의 읽지 않은 메시지 수 업데이트
        updateUnreadCountForParticipants(chatRoom.getId(), currentUserId);

        // WebSocket을 통해 실시간 메시지 전송
        StompChatMessageDTO stompMessage = StompChatMessageDTO.builder()
                .type(StompChatMessageDTO.TypeEnum.CHAT)
                .chatRoomId(roomId)
                .senderId(currentUserId)
                .content(message.getMessage())
                .timestamp(message.getCreatedAt())
                .build();

        messagingTemplate.convertAndSend("/topic/chatrooms/" + roomId, stompMessage);

        // 채팅방 참가자들에게 알림 전송
        chatRoom.getParticipants().forEach(participant -> {
            if (!participant.getMemberId().equals(currentUserId.toString())) {
                notificationService.createChatMessageNotification(
                        roomId,
                        currentUserId,
                        UUID.fromString(participant.getMemberId()),
                        savedMessage.getMessage(),
                        savedMessage.getCreatedAt()
                );
            }
        });

        log.info("메시지 전송 완료. 채팅방 ID: {}, 메시지 : {}", roomId, message.getMessage());
        return chatRoomMongoConverter.toMessageDto(savedMessage, currentUserId);
    }

    @Override
    public ChatRoomDTO getChatRoom(String roomId, UUID currentUserId) {
        return null;
    }

    private void updateUnreadCountForParticipants(String roomId, UUID senderId) {
        // 채팅방 참가자 조회
        List<ChatRoomSettingMongo> participantSettings = mongoChatRoomSettingRepository.findAllByChatRoomId(roomId);

        for (ChatRoomSettingMongo setting : participantSettings) {
            if (!setting.getMemberId().equals(senderId)) {
                // 발신자가 아닌 참가자의 읽지 않은 메시지 수 증가
                setting.incrementUnreadCount();
                mongoChatRoomSettingRepository.save(setting);
            }
        }
    }

    @Override
    public Page<ChatMessageDTO> getChatMessages(String roomId, UUID currentUserId, Pageable pageable) {
        log.info("채팅방 메시지 히스토리 조회 시작. 채팅방 ID: {}, 요청자 ID: {}", roomId, currentUserId);

        // 채팅방 조회
        ChatRoomMongo chatRoom = getChatRoom(roomId);

        // 참여 권한 확인
        isParticipant(chatRoom.getId(), currentUserId);

        // 메시지 조회 (시간순 - 오래된 메시지가 먼저 표시되도록)
        // 페이징 정보를 그대로 사용하되, 정렬 방향만 변경
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort().and(pageable.getSort().ascending())  // 오름차순으로 변경
        );

        Page<ChatMessageMongo> messages = mongoChatMessageRepository.findByChatRoomId(
                chatRoom.getId(),
                sortedPageable
        );

        // 읽음 상태로 표시
        markAsRead(roomId, currentUserId);

        notificationService.markChatRoomNotificationsAsRead(roomId, currentUserId);

        // DTO 변환 및 반환
        List<ChatMessageDTO> messageDTOs = messages.getContent().stream()
                .map(message -> chatRoomMongoConverter.toMessageDto(message, currentUserId))
                .collect(Collectors.toList());

        return new PageImpl<>(messageDTOs, pageable, messages.getTotalElements());
    }

    /**
     * 채팅방의 유효성을 검사하는 메서드 존재하지 않는 채팅방인 경우 예외 발생
     *
     * @param roomId 채팅방 ID
     * @throws ExceptionHandler 채팅방이 존재하지 않을 경우
     */
    @Override
    public void validateChatroom(String roomId) {
        if (!mongoChatRoomRepository.existsById(roomId)) {
            log.error("채팅방을 찾을 수 없음. ID: {}", roomId);
            throw new ExceptionHandler(ErrorStatus.CHATROOM_NOT_FOUND);
        }
        log.debug("채팅방 검증 완료. ID: {}", roomId);
    }

    /**
     * 특정 사용자가 채팅방에 참여하고 있는지 확인합니다.
     *
     * @param roomId   채팅방 ID
     * @param memberId 사용자 ID
     * @return 참여 여부
     */
    @Transactional(readOnly = true)
    public boolean isParticipant(String roomId, UUID memberId) {
        if (!mongoChatRoomRepository.existsByIdAndParticipantsMemberId(roomId, memberId.toString())) {
            log.error("채팅방에 참여하지 않은 사용자. 채팅방 ID: {}, 사용자 ID: {}", roomId, memberId);
            throw new ExceptionHandler(ErrorStatus.CHATROOM_ACCESS_DENIED);
        }
        return true;
    }

    private Member getMember(UUID targetUserId) {
        return memberRepository.findById(targetUserId)
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.MEMBER_NOT_FOUND));
    }

    private ChatRoomMongo getChatRoom(String roomId) {
        return getChatRoomMongo(roomId);
    }

    private ChatRoomMongo getChatRoomMongo(String roomId) {
        ChatRoomMongo chatRoom = mongoChatRoomRepository.findById(roomId)
                .orElseThrow(() -> {
                    log.error("채팅방을 찾을 수 없음. ID: {}", roomId);
                    return new ExceptionHandler(ErrorStatus.CHATROOM_NOT_FOUND);
                });
        return chatRoom;
    }

    /**
     * 메시지를 읽음 상태로 표시합니다.
     */
    private void markAsRead(String roomId, UUID memberId) {
        ChatRoomSettingMongo setting = mongoChatRoomRepository
                .findChatRoomSettingByRoomIdAndMemberId(roomId, memberId.toString())
                .orElseThrow(() -> new ExceptionHandler(ErrorStatus.CHATROOM_SETTING_NOT_FOUND));

        // 최신 메시지 찾기
        Optional<ChatMessageMongo> latestMessage = mongoChatMessageRepository
                .findTopByChatRoomIdOrderByCreatedAtDesc(roomId);

        // 읽지 않은 메시지 수를 0으로 초기화
        setting.resetUnreadCount();
        mongoChatRoomSettingRepository.save(setting);

        log.debug("메시지 읽음 상태로 표시. 채팅방 ID: {}, 사용자 ID: {}", roomId, memberId);
    }
}
