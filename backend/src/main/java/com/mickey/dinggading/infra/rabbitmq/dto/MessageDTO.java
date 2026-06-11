package com.mickey.dinggading.infra.rabbitmq.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO implements Serializable {

    private Object content;
    private UUID senderId;
    private Long attemptId;
    private String recordFilename;
    private String originalFilename;
    private Integer beat_score;
    private Integer tune_score;
    private Integer tone_score;
    private LocalDateTime timestamp;

}