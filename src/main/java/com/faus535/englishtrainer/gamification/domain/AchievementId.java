package com.faus535.englishtrainer.gamification.domain;

public record AchievementId(String value) {

    public AchievementId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("AchievementId cannot be null or blank");
        }
    }
}
