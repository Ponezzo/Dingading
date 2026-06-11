package com.mickey.dinggading.domain.attempt.service;

import com.mickey.dinggading.base.status.ErrorStatus;
import com.mickey.dinggading.domain.memberrank.model.Attempt;
import com.mickey.dinggading.domain.memberrank.model.RankMatching;
import com.mickey.dinggading.domain.memberrank.model.SongByInstrument;
import com.mickey.dinggading.exception.ExceptionHandler;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// 랭크 모드 전략
@Component
@RequiredArgsConstructor
public class RankAttemptCreationStrategy implements AttemptCreationStrategy {

    @Override
    public Attempt createAttempt(SongByInstrument songByInstrument, RankMatching rankMatching, UUID memberId) {
        // 랭크 매칭의 유효성 검증
        validateRankMatching(rankMatching, memberId);

        // 분석이 완료되기 전이므로 점수는 나중에 설정됨
        Attempt attempt = Attempt.createRankAttempt(rankMatching, songByInstrument);

        rankMatching.addAttempt(attempt);
        return attempt;
    }

    /**
     * 랭크 매칭의 유효성을 검증합니다.
     *
     * @param rankMatching 검증할 랭크 매칭
     * @param memberId     현재 회원 ID
     */
    private void validateRankMatching(RankMatching rankMatching, UUID memberId) {
        if (rankMatching.isAnalyzing()) {
            throw new ExceptionHandler(ErrorStatus.ANALYZING_NOT_FINISHED);
        }

        // 1. 매칭이 만료되었는지 확인
        if (rankMatching.isExpired()) {
            throw new ExceptionHandler(ErrorStatus.RANK_MATCHING_EXPIRED);
        }

        // 2. 랭크 매칭이 현재 진행 중인지 확인
        if (rankMatching.isFinished()) {
            throw new ExceptionHandler(ErrorStatus.RANK_MATCHING_FINISHED);
        }

        // 3. 최대 시도 횟수 초과 확인 (최대 5회)
        if (rankMatching.getAttemptCount() >= 5) {
            throw new ExceptionHandler(ErrorStatus.MAX_ATTEMPT_REACHED);
        }

        // 4. 회원이 해당 랭크 매칭의 소유자인지 확인
        if (!rankMatching.getMemberRank().getMember().getMemberId().equals(memberId)) {
            throw new ExceptionHandler(ErrorStatus._FORBIDDEN);
        }
    }
}