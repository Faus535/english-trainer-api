package com.faus535.englishtrainer.review.domain.event;

import com.faus535.englishtrainer.review.domain.ReviewItemId;

import java.util.UUID;

public record ReviewCompletedEvent(
        ReviewItemId itemId,
        UUID userId,
        boolean graduated
) {}
