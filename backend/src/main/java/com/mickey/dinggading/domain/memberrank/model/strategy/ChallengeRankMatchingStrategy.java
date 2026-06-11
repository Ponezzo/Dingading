package com.mickey.dinggading.domain.memberrank.model.strategy;

import com.mickey.dinggading.domain.memberrank.model.MemberRank;
import com.mickey.dinggading.domain.memberrank.model.RankMatching;
import java.time.LocalDateTime;

/**
 * 티어 도전 완료 처리 전략 상위 티어 도전을 위한 매칭 완료 처리 로직
 */
public class ChallengeRankMatchingStrategy implements RankMatchingCompletionStrategy {

    @Override
    public void processCompletion(LocalDateTime currentTime, RankMatching rankMatching, MemberRank memberRank) {
        if (rankMatching.isSuccessful()) {
            memberRank.promote(currentTime);
        }
        // 도전 실패: 티어 유지, 티어 유지 자격 갱신 없음 (아무 작업 없음)
    }
}