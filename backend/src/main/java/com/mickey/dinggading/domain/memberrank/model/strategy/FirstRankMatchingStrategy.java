package com.mickey.dinggading.domain.memberrank.model.strategy;

import com.mickey.dinggading.domain.memberrank.model.MemberRank;
import com.mickey.dinggading.domain.memberrank.model.RankMatching;
import java.time.LocalDateTime;

/**
 * 배치고사 완료 처리 전략 첫 티어 배정을 위한 매칭 완료 처리 로직
 */
public class FirstRankMatchingStrategy implements RankMatchingCompletionStrategy {
    @Override
    public void processCompletion(LocalDateTime currentTime, RankMatching rankMatching, MemberRank memberRank) {
        if (rankMatching.isSuccessful()) {
            memberRank.firstMatchingPromote(currentTime, rankMatching.getTargetTier());
        } else {
            // 배치고사 실패: UNRANKED 상태 유지, 마지막 도전 티어 기록
            memberRank.recordFailedPlacement(rankMatching.getTargetTier());
        }
    }
}