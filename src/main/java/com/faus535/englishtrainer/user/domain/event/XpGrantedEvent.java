package com.faus535.englishtrainer.user.domain.event;

import com.faus535.englishtrainer.shared.domain.event.DomainEvent;
import com.faus535.englishtrainer.user.domain.UserProfileId;

@DomainEvent
public record XpGrantedEvent(UserProfileId profileId, int amount, int totalXp) {}
