package com.faus535.englishtrainer.analytics.domain;

import java.util.List;
import java.util.UUID;

public interface LevelHistoryRepository {

    LevelHistoryEntry save(LevelHistoryEntry entry);

    List<LevelHistoryEntry> findByUserId(UUID userId);

    List<LevelHistoryEntry> findByUserIdAndModule(UUID userId, String module);
}
