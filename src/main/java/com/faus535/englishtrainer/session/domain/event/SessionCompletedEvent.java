package com.faus535.englishtrainer.session.domain.event;

import com.faus535.englishtrainer.shared.domain.event.DomainEvent;
import com.faus535.englishtrainer.session.domain.SessionId;
import com.faus535.englishtrainer.user.domain.UserProfileId;

@DomainEvent
public record SessionCompletedEvent(SessionId sessionId, UserProfileId userId) {}
