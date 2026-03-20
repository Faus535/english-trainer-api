package com.faus535.englishtrainer.gamification.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface JpaUserAchievementRepository extends JpaRepository<UserAchievementEntity, UUID> {

    List<UserAchievementEntity> findByUserId(UUID userId);

    boolean existsByUserIdAndAchievementId(UUID userId, String achievementId);
}
