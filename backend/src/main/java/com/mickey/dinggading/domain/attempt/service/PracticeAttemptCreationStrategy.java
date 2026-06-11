package com.mickey.dinggading.domain.attempt.service;

import com.mickey.dinggading.domain.memberrank.model.Attempt;
import com.mickey.dinggading.domain.memberrank.model.RankMatching;
import com.mickey.dinggading.domain.memberrank.model.SongByInstrument;
import java.util.UUID;

// 연습 모드 전략
public class PracticeAttemptCreationStrategy implements AttemptCreationStrategy {
    @Override
    public Attempt createAttempt(SongByInstrument songByInstrument, RankMatching rankMatching, UUID memberId) {
        return Attempt.createPracticeAttempt(
                songByInstrument
        );
    }
}