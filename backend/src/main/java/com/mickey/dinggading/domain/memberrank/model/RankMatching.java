package com.mickey.dinggading.domain.memberrank.model;

import com.mickey.dinggading.base.BaseEntity;
import com.mickey.dinggading.base.status.ErrorStatus;
import com.mickey.dinggading.exception.ExceptionHandler;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rank_matching")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class RankMatching extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rank_matching_id")
    private Long rankMatchingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_rank", nullable = false)
    private MemberRank memberRank;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MatchingStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_instrument_pack_id", nullable = false)
    private SongInstrumentPack songInstrumentPack;

    @Column(name = "expire_date", nullable = false)
    private LocalDate expireDate;

    @Column(name = "started_at", nullable = false)
    private LocalDate startedAt;

    @Column(name = "attempt_count", nullable = false)
    private Integer attemptCount;

    @Column(name = "success_count", nullable = false)
    private Integer successCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "instrument", nullable = false)
    private Instrument instrument;

    @Enumerated(EnumType.STRING)
    @Column(name = "rank_type", nullable = false)
    private RankType rankType;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_tier", nullable = false)
    private Tier targetTier;

    @OneToMany(mappedBy = "rankMatching", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attempt> attempts = new ArrayList<>();

    /**
     * 새로운 랭크 매칭을 생성합니다.
     */
    public static RankMatching createRankMatching(
            MemberRank memberRank,
            SongInstrumentPack songInstrumentPack,
            Instrument instrument,
            RankType rankType,
            Tier targetTier,
            LocalDateTime currentTime
    ) {
        validateRankTypeEligibility(rankType, memberRank, targetTier, songInstrumentPack, currentTime);

        LocalDate now = currentTime.toLocalDate();
        LocalDate expireDate = currentTime.toLocalDate().plusDays(7);

        return RankMatching.builder()
                .status(MatchingStatus.IN_PROGRESS)
                .songInstrumentPack(songInstrumentPack)
                .memberRank(memberRank)
                .expireDate(expireDate)
                .startedAt(now)
                .attemptCount(0)
                .successCount(0)
                .instrument(instrument)
                .rankType(rankType)
                .targetTier(targetTier)
                .attempts(new ArrayList<>())
                .build();
    }

    private static void validateRankTypeEligibility(RankType rankType, MemberRank memberRank, Tier targetTier,
                                                    SongInstrumentPack songPack, LocalDateTime currentTime) {

        if (songPack.getSongPackTier() != targetTier) {
            throw new ExceptionHandler(ErrorStatus.SONG_PACK_TIER_NOT_MATCHED_MATCHING);
        }

        if (rankType == RankType.FIRST) {
            if (memberRank.canFirst()) {
                throw new ExceptionHandler(ErrorStatus.ALREADY_RANKED);
            }
        }

        if (rankType == RankType.CHALLENGE) {
            if (memberRank.canChallenge()) {
                throw new ExceptionHandler(ErrorStatus.INVALID_TARGET_TIER);
            }
        }

        if (rankType == RankType.DEFENCE) {
            if (memberRank.isInDefencePeriod(currentTime)) {
                throw new ExceptionHandler(ErrorStatus.NOT_IN_DEFENCE_PERIOD);
            }
        }
    }

    /**
     * 시도를 진행하고 결과를 업데이트합니다.
     */
    public void addAttempt(Attempt attempt) {
        if (attemptCount >= 5) {
            throw new ExceptionHandler(ErrorStatus.MAX_ATTEMPT_REACHED);
        }

        if (attemptCount == 4) {
            this.status = MatchingStatus.ANALYZING; // 4 인경우
        }

        this.attemptCount++;
        attempt.updateRankMatching(this);
    }

    public void doMatchingResult(Attempt attempt, LocalDateTime currentTime) {

        if (attempt.isSuccess()) {
            this.successCount++;

            // 성공 횟수가 3 이상이면 완료 상태로 변경
            if (this.successCount >= 3) {
                this.status = MatchingStatus.COMPLETED;
            }
        }

        // 시도 횟수가 5회 이상이고 성공 횟수가 3회 미만이면 실패 상태로 변경
        if (this.attemptCount >= 5 && this.successCount < 3) {
            this.status = MatchingStatus.FAILED;
        }
    }

    /* ---------------------- 티어 변동 ----------------------*/

    public boolean isFirstRankType() {
        return this.rankType == RankType.FIRST;
    }

    public void updateMemberRank(MemberRank memberRank) {

        if (this.memberRank != null) {
            this.memberRank.getRankMatching().remove(this);
        }
        this.memberRank = memberRank;
        memberRank.getRankMatching().add(this);

    }


    /* ---------------------- 도전 날자 ----------------------*/

    /**
     * 만료 처리를 합니다.
     */
    public void expire() {
        if (this.status == MatchingStatus.IN_PROGRESS) {
            this.status = MatchingStatus.EXPIRED;
        }
    }

    public boolean isAnalyzing() {
        return this.status == MatchingStatus.ANALYZING;
    }

    public boolean isFinished() {
        return this.status != MatchingStatus.IN_PROGRESS;
    }

    /**
     * 만료 여부를 확인합니다.
     */
    public boolean isExpired() {
        return this.status == MatchingStatus.EXPIRED;
    }

    /**
     * 매칭이 성공적으로 완료되었는지 확인 3회 이상 성공했을 경우 매칭 성공으로 간주
     *
     * @return 매칭 성공 여부
     */
    public boolean isSuccessful() {
        return successCount >= 3;
    }

}