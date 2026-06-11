package com.mickey.dinggading.domain.memberrank.model;

import com.mickey.dinggading.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member_tier_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class MemberTierLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tier_log_id")
    private Long tierLogId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_rank_id", nullable = false)
    private MemberRank memberRank;

    @Enumerated(EnumType.STRING)
    @Column(name = "before_tier", nullable = false)
    private Tier beforeTier;

    @Enumerated(EnumType.STRING)
    @Column(name = "after_tier", nullable = false)
    private Tier afterTier;

    @Column(name = "changed_date", nullable = false)
    private LocalDate changedDate;

    /**
     * 새로운 티어 로그를 생성합니다.
     */
    public static MemberTierLog createTierLog(MemberRank memberRank, Tier beforeTier, Tier afterTier) {
        return MemberTierLog.builder()
                .memberRank(memberRank)
                .beforeTier(beforeTier)
                .afterTier(afterTier)
                .changedDate(LocalDate.now())
                .build();
    }
}