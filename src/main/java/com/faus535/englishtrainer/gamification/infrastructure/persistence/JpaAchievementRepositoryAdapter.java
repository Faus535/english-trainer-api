package com.faus535.englishtrainer.gamification.infrastructure.persistence;

import com.faus535.englishtrainer.gamification.domain.Achievement;
import com.faus535.englishtrainer.gamification.domain.AchievementId;
import com.faus535.englishtrainer.gamification.domain.AchievementRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class JpaAchievementRepositoryAdapter implements AchievementRepository {

    private final JpaAchievementRepository jpaRepository;

    JpaAchievementRepositoryAdapter(JpaAchievementRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Achievement> findAll() {
        return jpaRepository.findAll().stream()
                .map(AchievementEntity::toAggregate)
                .toList();
    }

    @Override
    public Optional<Achievement> findById(AchievementId id) {
        return jpaRepository.findById(id.value())
                .map(AchievementEntity::toAggregate);
    }
}
