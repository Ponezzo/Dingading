// 파일 경로: src/main/java/com/mickey/dinggading/domain/livehouse/dto/LivehouseSessionDTO.java
package com.mickey.dinggading.domain.livehouse.dto;

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
public class LivehouseSessionDTO {
    private Long livehouseId;
    private String sessionId;
    private String token;
    private Long participantId;
    private String nickname;
}