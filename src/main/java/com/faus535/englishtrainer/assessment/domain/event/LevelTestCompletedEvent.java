package com.faus535.englishtrainer.assessment.domain.event;

import com.faus535.englishtrainer.shared.domain.event.DomainEvent;
import com.faus535.englishtrainer.assessment.domain.LevelTestResultId;
import com.faus535.englishtrainer.user.domain.UserProfileId;

@DomainEvent
public record LevelTestCompletedEvent(LevelTestResultId resultId, UserProfileId userId) {}
