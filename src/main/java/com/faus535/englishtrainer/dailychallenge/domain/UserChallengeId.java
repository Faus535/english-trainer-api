package com.faus535.englishtrainer.dailychallenge.domain;

import java.util.UUID;

public record UserChallengeId(UUID value) {

    public UserChallengeId {
        if (value == null) {
            throw new IllegalArgumentException("UserChallengeId cannot be null");
        }
    }

    public static UserChallengeId generate() {
        return new UserChallengeId(UUID.randomUUID());
    }

    public static UserChallengeId fromString(String id) {
        return new UserChallengeId(UUID.fromString(id));
    }
}
