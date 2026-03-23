package com.faus535.englishtrainer.minigame.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface JpaMiniGameScoreRepository extends JpaRepository<MiniGameScoreEntity, UUID> {

    List<MiniGameScoreEntity> findByUserIdAndGameTypeOrderByPlayedAtDesc(UUID userId, String gameType);
}
