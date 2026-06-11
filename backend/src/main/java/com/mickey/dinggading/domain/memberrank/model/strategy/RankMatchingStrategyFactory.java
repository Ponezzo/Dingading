package com.mickey.dinggading.domain.memberrank.model.strategy;

import com.mickey.dinggading.domain.memberrank.model.RankType;

/**
 * 랭크 타입에 맞는 전략 객체를 생성하는 팩토리 클래스
 */
public class RankMatchingStrategyFactory {
    /**
     * 주어진 랭크 타입에 맞는 전략 객체 반환
     *
     * @param rankType 랭크 매칭 타입 (FIRST, CHALLENGE, DEFENCE)
     * @return 해당 타입에 맞는 전략 구현체
     * @throws IllegalArgumentException 알 수 없는 랭크 타입인 경우
     */
    public static RankMatchingCompletionStrategy getStrategy(RankType rankType) {
        switch (rankType) {
            case FIRST:
                return new FirstRankMatchingStrategy();
            case CHALLENGE:
                return new ChallengeRankMatchingStrategy();
            case DEFENCE:
                return new DefenceRankMatchingStrategy();
            default:
                throw new IllegalArgumentException("Unknown rank type: " + rankType);
        }
    }
}