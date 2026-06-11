// 파일 경로: src/main/java/com/mickey/dinggading/domain/livehouse/dto/ParticipantDTO.java
package com.mickey.dinggading.domain.livehouse.dto;

import com.mickey.dinggading.domain.livehouse.entity.Participant;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipantDTO {
    private Long participantId;
    private Long livehouseId;
    private String nickname;
    private String connectionId;
    private boolean isHost;
    private LocalDateTime joinedAt;

    public static ParticipantDTO fromEntity(Participant participant) {
        return ParticipantDTO.builder()
                .participantId(participant.getParticipantId())
                .livehouseId(participant.getLivehouse().getLivehouseId())
                .nickname(participant.getNickname())
                .connectionId(participant.getConnectionId())
                .isHost(participant.isHost())
                .joinedAt(participant.getJoinedAt())
                .build();
    }
}
