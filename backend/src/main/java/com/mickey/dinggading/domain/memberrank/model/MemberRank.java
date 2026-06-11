package com.mickey.dinggading.domain.memberrank.model;

import com.mickey.dinggading.base.BaseEntity;
import com.mickey.dinggading.domain.member.model.entity.Member;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member_rank")
@Getter
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberRank extends BaseEntity {

    private static final int DEFENCE_PERIOD_DAYS = 30;
    private static final int TIER_MAINTENANCE_PERIOD_DAYS = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_rank_id")
    private Long memberRankId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "instrument", nullable = false)
    private Instrument instrument;

    @Enumerated(EnumType.STRING)
    @Column(name = "tier", nullable = false)
    private Tier tier;

    @Column(name = "beat_score")
    private Integer beatScore;

    @Column(name = "tune_score")
    private Integer tuneScore;

    @Column(name = "tone_score")
    private Integer toneScore;

    @Column(name = "rank_success_count", columnDefinition = "int default 0")
    private Integer rankSuccessCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "last_attempt_tier")
    private Tier lastAttemptTier;

    @Column(name = "defence_expire_date")
    private LocalDateTime defenceExpireDate;

    @Column(name = "last_attempt_date")
    private LocalDateTime lastAttemptDate;

    @OneToMany(mappedBy = "memberRank", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RankMatching> rankMatching = new ArrayList<>();

    @OneToMany(mappedBy = "memberRank", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberTierLog> tierLogs = new ArrayList<>();

    /**
     * 새로운 멤버 랭크를 생성합니다.
     */
    public static MemberRank createMemberRank(Instrument instrument) {
        return MemberRank.builder()
                .member(null) // 초기 멤버는 null, updateMember로 업데이트 필요
                .instrument(instrument)
                .tier(Tier.UNRANKED)
                .beatScore(0)
                .tuneScore(0)
                .toneScore(0)
                .rankSuccessCount(0)
                .lastAttemptTier(Tier.DIAMOND) // 초기 null 상태
                .defenceExpireDate(LocalDateTime.of(2099, 1, 1, 0, 0))
                .lastAttemptDate(LocalDateTime.of(2099, 1, 1, 0, 0))
                .rankMatching(new ArrayList<>())
                .tierLogs(new ArrayList<>())
                .build();
    }

    /**
     * 티어 도전이 가능한지 여부 확인
     *
     * @return 도전 가능 여부
     */
    public boolean canChallenge(Tier targetTier) {

        return tier.isNextTier(targetTier) && canChallenge();

    }

    public boolean canChallenge() {
        // UNRANKED 상태인 경우 도전 불가능(배치고사 필요)
        if (Tier.UNRANKED.equals(tier)) {
            return false;
        }

        // DIAMOND는 최고 티어라 도전 불가능
        if (tier == Tier.DIAMOND) {
            return false;
        }

        return true;
    }

    /**
     * 배치고사할 수 있는지 여부
     *
     * @return
     */
    public boolean canFirst() {
        return this.tier.equals(Tier.UNRANKED) && this.rankSuccessCount == 0;
    }

    public boolean canFirst(Tier targetTier) {
        boolean b = this.lastAttemptTier.compareTo(targetTier) > 0;
        return this.tier.equals(Tier.UNRANKED) && this.rankSuccessCount == 0 && b;
    }

    /*
     * 멤버를 업데이트 합니다.
     */
    public void updateMember(Member member) {
        if (this.member != null) {
            this.member.getMemberRanks().remove(this);
        }

        this.member = member;
        member.getMemberRanks().add(this);
    }

    /**
     * 점수를 업데이트합니다.
     */
    public void updateScores(Integer beatScore, Integer tuneScore, Integer toneScore) {
        this.beatScore = beatScore;
        this.tuneScore = tuneScore;
        this.toneScore = toneScore;
    }

    /**
     * 티어를 업데이트합니다.
     */
    private void updateTier(LocalDateTime today, Tier newTier) {
        Tier oldTier = this.tier;
        this.tier = newTier;

        MemberTierLog tierLog = MemberTierLog.createTierLog(this, oldTier, newTier);
        this.tierLogs.add(tierLog);

    }

    /**
     * 티어 승급 처리 (도전 성공 시)
     *
     * @return 생성된 티어 로그
     */
    public void promote(LocalDateTime currentTime) {
        if (Tier.DIAMOND.equals(this.tier)) {
            return;
        }

        Tier promoteTier = Tier.values()[this.tier.ordinal() + 1];
        updateTier(currentTime, promoteTier);
        renewTierQualification(currentTime, promoteTier);
        incrementSuccessCount();

    }

    public void keep(LocalDateTime currentTime) {
        updateTier(currentTime, this.tier);
        renewTierQualification(currentTime, this.tier);
        incrementSuccessCount();
    }

    /**
     * 티어 강등 처리 방어 실패 시 현재 티어에서 한 단계 낮은 티어로 강등 단, IRON 티어는 더 이상 강등되지 않음
     */
    public void demote(LocalDateTime currentTime) {
        if (tier == Tier.IRON) {
            return;
        }

        Tier demoteTier = Tier.values()[this.tier.ordinal() - 1];
        updateTier(currentTime, demoteTier);
        renewTierQualification(currentTime, demoteTier);

    }

    /**
     * 배치고사로 성공했을 때 티어 승급시키기
     *
     * @param today
     * @param targetTier
     */
    public void firstMatchingPromote(LocalDateTime today, Tier targetTier) {
        updateTier(today, targetTier);
        renewTierQualification(today, targetTier);
        incrementSuccessCount();
    }

    /**
     * 랭크 매칭 성공 시 티어 유지 자격 갱신
     */
    private void renewTierQualification(LocalDateTime today, Tier attemptedTier) {
        // 랭크 매칭 성공 후 30일 동안의 티어 유지 자격 부여
        this.lastAttemptDate = today;
        // 티어 유지 자격 만료일 갱신
        this.lastAttemptTier = attemptedTier;

        this.defenceExpireDate = lastAttemptDate.plusDays(TIER_MAINTENANCE_PERIOD_DAYS).plusDays(DEFENCE_PERIOD_DAYS);

    }

    /**
     * 현재 티어 방어가 필요한지 여부 확인 티어 유지 자격이 만료되었고 현재 UNRANKED가 아닌 경우 방어가 필요함
     *
     * @param currentTime 현재 시간
     * @return 방어 필요 여부
     */
    public boolean isInDefencePeriod(LocalDateTime currentTime) {
        // UNRANKED 상태는 방어가 필요 없음
        if (tier == Tier.UNRANKED || tier == Tier.IRON) {
            return false;
        }

        // 현재 날짜가 티어 유지 자격 만료일 이후인 경우 방어 필요
        return currentTime.isAfter(lastAttemptDate.plusDays(TIER_MAINTENANCE_PERIOD_DAYS)) &&
                currentTime.isBefore(defenceExpireDate);
    }

    /**
     * 랭크 성공 횟수 증가 도전 성공 시 성공 횟수를 1 증가시킴
     */
    private void incrementSuccessCount() {
        this.rankSuccessCount += 1;
    }

    /**
     * 배치고사 실패 기록 배치고사 실패 시 마지막으로 도전한 티어 정보 기록
     *
     * @param attemptedTier 도전했던 티어
     */
    public void recordFailedPlacement(Tier attemptedTier) {
        this.lastAttemptTier = attemptedTier;
    }

}