package com.faus535.englishtrainer.gamification.domain.event;

import com.faus535.englishtrainer.shared.domain.event.DomainEvent;
import com.faus535.englishtrainer.gamification.domain.AchievementId;
import com.faus535.englishtrainer.user.domain.UserProfileId;

@DomainEvent
public record AchievementUnlockedEvent(UserProfileId userId, AchievementId achievementId) {}
