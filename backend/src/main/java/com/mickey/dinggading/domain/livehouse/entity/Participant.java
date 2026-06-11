// 파일 경로: src/main/java/com/mickey/dinggading/domain/livehouse/entity/Participant.java
package com.mickey.dinggading.domain.livehouse.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "participants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Participant {

    @Id
    @Column(name = "participant_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long participantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "livehouse_id", nullable = false)
    private Livehouse livehouse;

    @Column(nullable = false)
    private String nickname;

    @Column(name = "connection_id")
    private String connectionId;

    @Column(name = "is_host", nullable = false)
    private boolean isHost;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

}
