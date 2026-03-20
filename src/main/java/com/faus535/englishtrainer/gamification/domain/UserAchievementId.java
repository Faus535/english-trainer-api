package com.faus535.englishtrainer.gamification.domain;

import java.util.UUID;

public record UserAchievementId(UUID value) {

    public UserAchievementId {
        if (value == null) {
            throw new IllegalArgumentException("UserAchievementId cannot be null");
        }
    }

    public static UserAchievementId generate() {
        return new UserAchievementId(UUID.randomUUID());
    }
}
