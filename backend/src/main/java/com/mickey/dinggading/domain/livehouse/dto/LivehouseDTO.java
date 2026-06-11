package com.mickey.dinggading.domain.livehouse.dto;

import com.mickey.dinggading.domain.livehouse.entity.Livehouse;
import java.time.LocalDateTime;
import java.util.UUID;
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
public class LivehouseDTO {
    private Long livehouseId;
    private String title;
    private String description;
    private UUID hostId;
    private String hostNickname;
    private LocalDateTime createdAt;
    private Integer participantCount;
    private Integer maxParticipants;
    private String status;

    public static LivehouseDTO fromEntity(Livehouse livehouse) {
        return LivehouseDTO.builder()
                .livehouseId(livehouse.getLivehouseId())
                .title(livehouse.getTitle())
                .description(livehouse.getDescription())
                .hostId(livehouse.getHostId())
                .hostNickname(livehouse.getHostNickname())
                .createdAt(livehouse.getCreatedAt())
                .participantCount(livehouse.getParticipantCount())
                .maxParticipants(livehouse.getMaxParticipants())
                .status(livehouse.getStatus().name())
                .build();
    }
}
