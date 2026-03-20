package com.faus535.englishtrainer.gamification.domain;

import com.faus535.englishtrainer.user.domain.UserProfileId;

public final class UserAchievementMother {

    private UserAchievementMother() {
    }

    public static UserAchievement create() {
        return UserAchievement.create(
                UserProfileId.generate(),
                new AchievementId("first-lesson")
        );
    }

    public static UserAchievement withUserAndAchievement(UserProfileId userId, AchievementId achievementId) {
        return UserAchievement.create(userId, achievementId);
    }
}
