package com.faus535.englishtrainer.gamification.infrastructure.persistence;

import com.faus535.englishtrainer.gamification.domain.AchievementId;
import com.faus535.englishtrainer.gamification.domain.UserAchievement;
import com.faus535.englishtrainer.gamification.domain.UserAchievementRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
class JpaUserAchievementRepositoryAdapter implements UserAchievementRepository {

    private final JpaUserAchievementRepository jpaRepository;

    JpaUserAchievementRepositoryAdapter(JpaUserAchievementRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<UserAchievement> findByUser(UserProfileId userId) {
        return jpaRepository.findByUserId(userId.value()).stream()
                .map(UserAchievementEntity::toAggregate)
                .toList();
    }

    @Override
    public boolean existsByUserAndAchievement(UserProfileId userId, AchievementId achievementId) {
        return jpaRepository.existsByUserIdAndAchievementId(userId.value(), achievementId.value());
    }

    @Override
    public List<UserAchievement> findByUserAndUnlockedAtAfter(UserProfileId userId, Instant since) {
        return jpaRepository.findByUserIdAndUnlockedAtAfter(userId.value(), since).stream()
                .map(UserAchievementEntity::toAggregate)
                .toList();
    }

    @Override
    public List<UserAchievement> findTop3ByUserOrderByUnlockedAtDesc(UserProfileId userId) {
        return jpaRepository.findTop3ByUserIdOrderByUnlockedAtDesc(userId.value()).stream()
                .map(UserAchievementEntity::toAggregate)
                .toList();
    }

    @Override
    public UserAchievement save(UserAchievement userAchievement) {
        UserAchievementEntity entity = UserAchievementEntity.fromAggregate(userAchievement);
        if (jpaRepository.existsById(userAchievement.id().value())) {
            entity.markAsExisting();
        }
        return jpaRepository.save(entity).toAggregate();
    }
}
