package com.faus535.englishtrainer.minimalpair.domain;

import com.faus535.englishtrainer.shared.domain.AggregateRoot;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.time.Instant;

public final class MinimalPairResult extends AggregateRoot<MinimalPairResultId> {

    private final MinimalPairResultId id;
    private final UserProfileId userId;
    private final MinimalPairId pairId;
    private final boolean correct;
    private final Instant answeredAt;

    private MinimalPairResult(MinimalPairResultId id, UserProfileId userId, MinimalPairId pairId,
                               boolean correct, Instant answeredAt) {
        this.id = id;
        this.userId = userId;
        this.pairId = pairId;
        this.correct = correct;
        this.answeredAt = answeredAt;
    }

    public static MinimalPairResult create(UserProfileId userId, MinimalPairId pairId, boolean correct) {
        return new MinimalPairResult(MinimalPairResultId.generate(), userId, pairId, correct, Instant.now());
    }

    public static MinimalPairResult reconstitute(MinimalPairResultId id, UserProfileId userId,
                                                   MinimalPairId pairId, boolean correct, Instant answeredAt) {
        return new MinimalPairResult(id, userId, pairId, correct, answeredAt);
    }

    public MinimalPairResultId id() { return id; }
    public UserProfileId userId() { return userId; }
    public MinimalPairId pairId() { return pairId; }
    public boolean correct() { return correct; }
    public Instant answeredAt() { return answeredAt; }
}
