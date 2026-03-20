package com.faus535.englishtrainer.gamification.domain;

import com.faus535.englishtrainer.gamification.domain.event.AchievementUnlockedEvent;
import com.faus535.englishtrainer.shared.domain.AggregateRoot;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.time.Instant;

public final class UserAchievement extends AggregateRoot<UserAchievementId> {

    private final UserAchievementId id;
    private final UserProfileId userId;
    private final AchievementId achievementId;
    private final Instant unlockedAt;

    private UserAchievement(UserAchievementId id, UserProfileId userId, AchievementId achievementId, Instant unlockedAt) {
        this.id = id;
        this.userId = userId;
        this.achievementId = achievementId;
        this.unlockedAt = unlockedAt;
    }

    public static UserAchievement create(UserProfileId userId, AchievementId achievementId) {
        UserAchievement ua = new UserAchievement(UserAchievementId.generate(), userId, achievementId, Instant.now());
        ua.registerEvent(new AchievementUnlockedEvent(userId, achievementId));
        return ua;
    }

    public static UserAchievement reconstitute(UserAchievementId id, UserProfileId userId,
                                                AchievementId achievementId, Instant unlockedAt) {
        return new UserAchievement(id, userId, achievementId, unlockedAt);
    }

    public UserAchievementId id() { return id; }
    public UserProfileId userId() { return userId; }
    public AchievementId achievementId() { return achievementId; }
    public Instant unlockedAt() { return unlockedAt; }
}
