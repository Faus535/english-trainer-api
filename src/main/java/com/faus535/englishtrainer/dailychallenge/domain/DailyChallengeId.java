package com.faus535.englishtrainer.dailychallenge.domain;

import java.util.UUID;

public record DailyChallengeId(UUID value) {

    public DailyChallengeId {
        if (value == null) {
            throw new IllegalArgumentException("DailyChallengeId cannot be null");
        }
    }

    public static DailyChallengeId generate() {
        return new DailyChallengeId(UUID.randomUUID());
    }

    public static DailyChallengeId fromString(String id) {
        return new DailyChallengeId(UUID.fromString(id));
    }
}
