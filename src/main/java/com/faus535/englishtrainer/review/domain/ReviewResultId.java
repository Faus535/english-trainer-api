package com.faus535.englishtrainer.review.domain;

import java.util.UUID;

public record ReviewResultId(UUID value) {

    public ReviewResultId {
        if (value == null) {
            throw new IllegalArgumentException("ReviewResultId cannot be null");
        }
    }

    public static ReviewResultId generate() {
        return new ReviewResultId(UUID.randomUUID());
    }
}
