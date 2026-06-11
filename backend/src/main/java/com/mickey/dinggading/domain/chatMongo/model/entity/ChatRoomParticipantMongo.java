package com.mickey.dinggading.domain.chatMongo.model.entity;

import com.mickey.dinggading.domain.member.model.entity.Member;
import com.mickey.dinggading.model.ParticipantRole;
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
@Document(collection = "chat_room_participant")
public class ChatRoomParticipantMongo {

    @Id
    private String chatRoomParticipantId;

    private String chatRoomId;

    private String memberId;

    private ParticipantRole role;
    private LocalDateTime joinedAt;

    public ChatRoomParticipantMongo(ChatRoomMongo chatRoom, Member member, ParticipantRole role) {
        this.chatRoomId = chatRoom.getId();
        this.memberId = member.getMemberId().toString();
        this.role = role;
        this.joinedAt = LocalDateTime.now();
    }

    public void updateRole(ParticipantRole role) {
        this.role = role;
    }

    public void setChatRoom(ChatRoomMongo chatRoom) {
        this.chatRoomId = chatRoom.getId();
    }
}