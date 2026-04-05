package com.faus535.englishtrainer.review.domain;

import java.util.UUID;

public record ReviewItemId(UUID value) {

    public ReviewItemId {
        if (value == null) {
            throw new IllegalArgumentException("ReviewItemId cannot be null");
        }
    }

    public static ReviewItemId generate() {
        return new ReviewItemId(UUID.randomUUID());
    }
}
