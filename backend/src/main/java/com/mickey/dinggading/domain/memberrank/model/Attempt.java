package com.mickey.dinggading.domain.memberrank.model;

import com.mickey.dinggading.base.BaseEntity;
import com.mickey.dinggading.base.status.ErrorStatus;
import com.mickey.dinggading.domain.memberrank.model.strategy.RankMatchingCompletionStrategy;
import com.mickey.dinggading.domain.memberrank.model.strategy.RankMatchingStrategyFactory;
import com.mickey.dinggading.exception.ExceptionHandler;
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
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "attempt")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Attempt extends BaseEntity {

    private static final int SCORE_INIT = -1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attempt_id")
    private Long attemptId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rank_matching_id", nullable = true)
    private RankMatching rankMatching;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_by_instrument_id", nullable = false)
    private SongByInstrument songByInstrument;

    @Column(name = "tune_score")
    private Integer tuneScore;

    @Column(name = "tone_score")
    private Integer toneScore;

    @Column(name = "beat_score")
    private Integer beatScore;

    @Column(name = "total_score", nullable = false)
    private Integer totalScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AttemptStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "game_type", nullable = false)
    private GameType gameType;

    @Enumerated(EnumType.STRING)
    @Column(name = "rank_type")
    private RankType rankType;

    /**
     * 새로운 랭크 모드 시도를 생성합니다.
     */
    public static Attempt createRankAttempt(RankMatching rankMatching,
                                            SongByInstrument songByInstrument) {

        boolean isDuplicated = isIsDuplicated(rankMatching, songByInstrument);

        if (isDuplicated) {
            throw new ExceptionHandler(ErrorStatus.DUPLICATE_SONG_ATTEMPT);
        }

        if (!rankMatching.getSongInstrumentPack().hasSongByInstrument(songByInstrument)) {
            throw new ExceptionHandler(ErrorStatus.SONG_PACK_TIER_NOT_MATCHED_MATCHING);
        }

        return Attempt.builder()
                .rankMatching(rankMatching)
                .songByInstrument(songByInstrument)
                .tuneScore(SCORE_INIT)
                .toneScore(SCORE_INIT)
                .beatScore(SCORE_INIT)
                .totalScore(SCORE_INIT)
                .status(AttemptStatus.BEFORE_ANALYZE)
                .gameType(GameType.RANK)
                .rankType(rankMatching.getRankType())
                .build();
    }

    private static boolean isIsDuplicated(RankMatching rankMatching, SongByInstrument songByInstrument) {
        // 동일한 SongByInstrumentId로 이미 시도한 적이 있는지 확인
        return rankMatching.getAttempts().stream()
                .anyMatch(attempt -> attempt.getSongByInstrument().getSongByInstrumentId()
                        .equals(songByInstrument.getSongByInstrumentId()));
    }

    /**
     * 새로운 연습 모드 시도를 생성합니다.
     */
    public static Attempt createPracticeAttempt(SongByInstrument songByInstrument) {

        return Attempt.builder()
                .songByInstrument(songByInstrument)
                .tuneScore(SCORE_INIT)
                .toneScore(SCORE_INIT)
                .beatScore(SCORE_INIT)
                .totalScore(SCORE_INIT)
                .status(AttemptStatus.BEFORE_ANALYZE)
                .gameType(GameType.PRACTICE)
                .build();
    }

    // 점수 설정 메서드
    public void updateScore(Integer beatScore, Integer tuneScore, Integer toneScore, LocalDateTime currentTime) {
        this.beatScore = beatScore;
        this.tuneScore = tuneScore;
        this.toneScore = toneScore;
        this.totalScore = calculateTotalScore();
        this.status = determineStatus();

        if (gameType == GameType.RANK) {
            notifyScoreUpdated(currentTime);
        }

    }

    // 점수 업데이트 이벤트 알림
    private void notifyScoreUpdated(LocalDateTime currentTime) {
        rankMatching.doMatchingResult(this, currentTime);

        // 전략 패턴을 활용해 게임 타입별 처리 로직 분리
        // 랭크 타입에 맞는 전략 선택 및 실행
        if (rankMatching.isFinished()) {
            RankMatchingCompletionStrategy strategy =
                    RankMatchingStrategyFactory.getStrategy(rankMatching.getRankType());
            strategy.processCompletion(currentTime, rankMatching, rankMatching.getMemberRank());
        }

    }

    // 성공 여부 판단
    private AttemptStatus determineStatus() {
        return totalScore >= 60 ? AttemptStatus.SUCCESS : AttemptStatus.FAIL;
    }

    /**
     * 총점을 계산합니다. (세 가지 점수의 평균)
     */
    private int calculateTotalScore() {
        return (this.tuneScore + this.toneScore + this.beatScore) / 3;
    }

    public boolean isSuccess() {
        return status == AttemptStatus.SUCCESS;
    }

    // 점수가 목표 티어에 충분한지 확인
    private boolean isScoreSufficient() {
        // 모든 티어에 대해 60점 이상을 합격으로 통일
        return totalScore >= 60;
    }

    public void updateRankMatching(RankMatching rankMatching) {
        if (rankMatching != null) {
            this.rankMatching.getAttempts().remove(this);
        }

        this.rankMatching = rankMatching;
        rankMatching.getAttempts().add(this);
    }
}