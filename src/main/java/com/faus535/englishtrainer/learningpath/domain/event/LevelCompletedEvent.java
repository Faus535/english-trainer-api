package com.faus535.englishtrainer.learningpath.domain.event;

import com.faus535.englishtrainer.learningpath.domain.LearningPathId;
import com.faus535.englishtrainer.shared.domain.event.DomainEvent;
import com.faus535.englishtrainer.user.domain.UserProfileId;

@DomainEvent
public record LevelCompletedEvent(LearningPathId pathId, UserProfileId userId, String level) {}
