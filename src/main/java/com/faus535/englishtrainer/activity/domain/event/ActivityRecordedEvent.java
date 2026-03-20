package com.faus535.englishtrainer.activity.domain.event;

import com.faus535.englishtrainer.shared.domain.event.DomainEvent;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.time.LocalDate;

@DomainEvent
public record ActivityRecordedEvent(UserProfileId userId, LocalDate date) {}
