package com.faus535.englishtrainer.gamification.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

interface JpaAchievementRepository extends JpaRepository<AchievementEntity, String> {
}
