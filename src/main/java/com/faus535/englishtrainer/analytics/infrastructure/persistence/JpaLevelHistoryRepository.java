package com.faus535.englishtrainer.analytics.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface JpaLevelHistoryRepository extends JpaRepository<LevelHistoryEntity, UUID> {

    List<LevelHistoryEntity> findByUserIdOrderByChangedAtAsc(UUID userId);

    List<LevelHistoryEntity> findByUserIdAndModuleOrderByChangedAtAsc(UUID userId, String module);
}
