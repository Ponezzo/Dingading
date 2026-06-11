package com.mickey.dinggading.domain.memberrank.model.strategy;

import com.mickey.dinggading.domain.memberrank.model.MemberRank;
import com.mickey.dinggading.domain.memberrank.model.RankMatching;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 티어 방어 완료 처리 전략 현재 티어 방어를 위한 매칭 완료 처리 로직
 */
@Component
@RequiredArgsConstructor
public class DefenceRankMatchingStrategy implements RankMatchingCompletionStrategy {

    @Override
    public void processCompletion(LocalDateTime currentTime, RankMatching rankMatching, MemberRank memberRank) {
        if (rankMatching.isSuccessful()) {
            // 방어 성공: 현재 티어 유지하고 30일 티어 유지 자격 부여
            memberRank.keep(currentTime);
        } else {
            // 방어 실패: 한 단계 낮은 티어로 강등하고 30일 티어 유지 자격 부여
            memberRank.demote(currentTime);
        }
    }
}