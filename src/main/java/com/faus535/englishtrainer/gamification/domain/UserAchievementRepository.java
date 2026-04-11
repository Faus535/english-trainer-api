package com.faus535.englishtrainer.gamification.domain;

import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.time.Instant;
import java.util.List;

public interface UserAchievementRepository {

    List<UserAchievement> findByUser(UserProfileId userId);

    boolean existsByUserAndAchievement(UserProfileId userId, AchievementId achievementId);

    List<UserAchievement> findByUserAndUnlockedAtAfter(UserProfileId userId, Instant since);

    List<UserAchievement> findTop3ByUserOrderByUnlockedAtDesc(UserProfileId userId);

    UserAchievement save(UserAchievement userAchievement);
}
