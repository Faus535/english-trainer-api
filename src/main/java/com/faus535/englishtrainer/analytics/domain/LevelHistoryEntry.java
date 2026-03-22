package com.faus535.englishtrainer.analytics.domain;

import java.time.Instant;
import java.util.UUID;

public record LevelHistoryEntry(
        UUID id,
        UUID userId,
        String module,
        String level,
        Instant changedAt
) {

    public static LevelHistoryEntry create(UUID userId, String module, String level) {
        return new LevelHistoryEntry(UUID.randomUUID(), userId, module, level, Instant.now());
    }
}
