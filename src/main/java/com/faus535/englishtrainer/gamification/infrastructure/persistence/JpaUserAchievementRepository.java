package com.faus535.englishtrainer.gamification.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

interface JpaUserAchievementRepository extends JpaRepository<UserAchievementEntity, UUID> {

    List<UserAchievementEntity> findByUserId(UUID userId);

    boolean existsByUserIdAndAchievementId(UUID userId, String achievementId);

    @Query("SELECT ua FROM UserAchievementEntity ua WHERE ua.userId = :userId AND ua.unlockedAt > :since")
    List<UserAchievementEntity> findByUserIdAndUnlockedAtAfter(UUID userId, Instant since);

    List<UserAchievementEntity> findTop3ByUserIdOrderByUnlockedAtDesc(UUID userId);
}
