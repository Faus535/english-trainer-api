package com.faus535.englishtrainer.gamification.domain;

import com.faus535.englishtrainer.gamification.domain.event.AchievementUnlockedEvent;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserAchievementTest {

    @Test
    void shouldCreateUserAchievement() {
        UserProfileId userId = UserProfileId.generate();
        AchievementId achievementId = new AchievementId("first-lesson");

        UserAchievement ua = UserAchievement.create(userId, achievementId);

        assertNotNull(ua.id());
        assertEquals(userId, ua.userId());
        assertEquals(achievementId, ua.achievementId());
        assertNotNull(ua.unlockedAt());
    }

    @Test
    void shouldRegisterAchievementUnlockedEvent() {
        UserProfileId userId = UserProfileId.generate();
        AchievementId achievementId = new AchievementId("first-lesson");

        UserAchievement ua = UserAchievement.create(userId, achievementId);

        var events = ua.pullDomainEvents();

        assertFalse(events.isEmpty());
        assertEquals(1, events.size());
        assertTrue(events.getFirst() instanceof AchievementUnlockedEvent);
        AchievementUnlockedEvent event = (AchievementUnlockedEvent) events.getFirst();
        assertEquals(userId, event.userId());
        assertEquals(achievementId, event.achievementId());
    }
}
