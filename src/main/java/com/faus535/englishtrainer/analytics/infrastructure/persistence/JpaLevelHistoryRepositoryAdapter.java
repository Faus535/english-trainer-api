package com.faus535.englishtrainer.analytics.infrastructure.persistence;

import com.faus535.englishtrainer.analytics.domain.LevelHistoryEntry;
import com.faus535.englishtrainer.analytics.domain.LevelHistoryRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
class JpaLevelHistoryRepositoryAdapter implements LevelHistoryRepository {

    private final JpaLevelHistoryRepository jpaRepository;

    JpaLevelHistoryRepositoryAdapter(JpaLevelHistoryRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public LevelHistoryEntry save(LevelHistoryEntry entry) {
        return jpaRepository.save(LevelHistoryEntity.fromDomain(entry)).toDomain();
    }

    @Override
    public List<LevelHistoryEntry> findByUserId(UUID userId) {
        return jpaRepository.findByUserIdOrderByChangedAtAsc(userId).stream()
                .map(LevelHistoryEntity::toDomain).toList();
    }

    @Override
    public List<LevelHistoryEntry> findByUserIdAndModule(UUID userId, String module) {
        return jpaRepository.findByUserIdAndModuleOrderByChangedAtAsc(userId, module).stream()
                .map(LevelHistoryEntity::toDomain).toList();
    }
}
