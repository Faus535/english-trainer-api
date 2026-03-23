package com.faus535.englishtrainer.minigame.domain;

import java.util.UUID;

public record MiniGameScoreId(UUID value) {

    public MiniGameScoreId {
        if (value == null) {
            throw new IllegalArgumentException("MiniGameScoreId cannot be null");
        }
    }

    public static MiniGameScoreId generate() {
        return new MiniGameScoreId(UUID.randomUUID());
    }
}
