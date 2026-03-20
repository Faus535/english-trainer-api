package com.faus535.englishtrainer.gamification.infrastructure;

import com.faus535.englishtrainer.gamification.domain.AchievementId;
import com.faus535.englishtrainer.gamification.domain.UserAchievement;
import com.faus535.englishtrainer.gamification.domain.UserAchievementId;
import com.faus535.englishtrainer.gamification.domain.UserAchievementRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class InMemoryUserAchievementRepository implements UserAchievementRepository {

    private final Map<UserAchievementId, UserAchievement> store = new HashMap<>();

    @Override
    public List<UserAchievement> findByUser(UserProfileId userId) {
        return store.values().stream()
                .filter(ua -> ua.userId().equals(userId))
                .toList();
    }

    @Override
    public boolean existsByUserAndAchievement(UserProfileId userId, AchievementId achievementId) {
        return store.values().stream()
                .anyMatch(ua -> ua.userId().equals(userId) && ua.achievementId().equals(achievementId));
    }

    @Override
    public UserAchievement save(UserAchievement userAchievement) {
        store.put(userAchievement.id(), userAchievement);
        return userAchievement;
    }
}
