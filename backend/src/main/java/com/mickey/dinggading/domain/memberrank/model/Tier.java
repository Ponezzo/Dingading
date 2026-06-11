package com.mickey.dinggading.domain.memberrank.model;

public enum Tier {
    UNRANKED(0), IRON(1), BRONZE(2), SILVER(3), GOLD(4), PLATINUM(5), DIAMOND(6);

    private final int value;

    Tier(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * 도전 모드를 진행할 때 사용
     *
     * @param targetTier
     * @return
     */
    public boolean isNextTier(Tier targetTier) {
        return targetTier.value - this.value == 1;
    }
}