package com.mickey.dinggading.domain.memberrank.model.strategy;

import com.mickey.dinggading.domain.memberrank.model.MemberRank;
import com.mickey.dinggading.domain.memberrank.model.RankMatching;
import java.time.LocalDateTime;

/**
 * 랭크 매칭 완료 처리 전략 인터페이스 각 랭크 타입(배치, 도전, 방어)별로 다른 완료 처리 로직을 구현하기 위한 전략 패턴
 */
public interface RankMatchingCompletionStrategy {
    /**
     * 랭크 매칭 완료 처리 수행
     *
     * @param rankMatching 처리할 랭크 매칭 정보
     * @param memberRank   회원의 랭크 정보
     */
    void processCompletion(LocalDateTime currentTime, RankMatching rankMatching, MemberRank memberRank);
}