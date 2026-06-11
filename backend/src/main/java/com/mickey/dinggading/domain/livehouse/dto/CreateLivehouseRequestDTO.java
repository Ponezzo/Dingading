// 파일 경로: src/main/java/com/mickey/dinggading/domain/livehouse/dto/CreateLivehouseRequestDTO.java
package com.mickey.dinggading.domain.livehouse.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateLivehouseRequestDTO {
    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    private String description;

    @NotNull(message = "최대 참여자 수는 필수입니다.")
    @Min(value = 2, message = "최소 2명 이상이어야 합니다.")
    @Max(value = 10, message = "최대 10명까지 가능합니다.")
    private Integer maxParticipants;
}