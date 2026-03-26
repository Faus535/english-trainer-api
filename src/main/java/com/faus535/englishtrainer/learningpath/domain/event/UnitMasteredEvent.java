package com.faus535.englishtrainer.learningpath.domain.event;

import com.faus535.englishtrainer.learningpath.domain.LearningPathId;
import com.faus535.englishtrainer.learningpath.domain.LearningUnitId;
import com.faus535.englishtrainer.shared.domain.event.DomainEvent;
import com.faus535.englishtrainer.user.domain.UserProfileId;

@DomainEvent
public record UnitMasteredEvent(LearningUnitId unitId, LearningPathId pathId, UserProfileId userId) {}
