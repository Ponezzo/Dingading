package com.mickey.dinggading.domain.attempt.service;

import com.mickey.dinggading.domain.memberrank.model.Attempt;
import com.mickey.dinggading.domain.memberrank.model.RankMatching;
import com.mickey.dinggading.domain.memberrank.model.SongByInstrument;
import java.util.UUID;

public interface AttemptCreationStrategy {
    Attempt createAttempt(SongByInstrument songByInstrument, RankMatching rankMatching, UUID memberId);
}