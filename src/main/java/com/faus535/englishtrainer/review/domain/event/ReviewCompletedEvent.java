package com.faus535.englishtrainer.review.domain.event;

import com.faus535.englishtrainer.review.domain.ReviewItemId;
import com.faus535.englishtrainer.shared.domain.event.DomainEvent;

import java.util.UUID;

@DomainEvent
public record ReviewCompletedEvent(
        ReviewItemId itemId,
        UUID userId,
        boolean graduated
) {}
