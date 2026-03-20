package com.faus535.englishtrainer.activity.domain;

import java.util.UUID;

public record ActivityDateId(UUID value) {

    public ActivityDateId {
        if (value == null) {
            throw new IllegalArgumentException("ActivityDateId cannot be null");
        }
    }

    public static ActivityDateId generate() {
        return new ActivityDateId(UUID.randomUUID());
    }

    public static ActivityDateId fromString(String id) {
        return new ActivityDateId(UUID.fromString(id));
    }
}
