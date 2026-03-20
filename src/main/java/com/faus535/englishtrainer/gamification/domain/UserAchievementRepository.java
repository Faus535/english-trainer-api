package com.faus535.englishtrainer.gamification.domain;

import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.util.List;

public interface UserAchievementRepository {

    List<UserAchievement> findByUser(UserProfileId userId);

    boolean existsByUserAndAchievement(UserProfileId userId, AchievementId achievementId);

    UserAchievement save(UserAchievement userAchievement);
}
