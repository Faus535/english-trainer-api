package com.faus535.englishtrainer.review.domain;

import java.time.Instant;
import java.util.UUID;

public record ReviewResult(ReviewResultId id, ReviewItemId itemId, UUID userId, int quality, Instant reviewedAt) {

    public static ReviewResult create(ReviewItemId itemId, UUID userId, int quality) {
        return new ReviewResult(ReviewResultId.generate(), itemId, userId, quality, Instant.now());
    }
}
