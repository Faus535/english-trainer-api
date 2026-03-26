package com.faus535.englishtrainer.learningpath.domain;

import java.util.UUID;

public record LearningPathId(UUID value) {

    public LearningPathId {
        if (value == null) {
            throw new IllegalArgumentException("LearningPathId cannot be null");
        }
    }

    public static LearningPathId generate() {
        return new LearningPathId(UUID.randomUUID());
    }

    public static LearningPathId fromString(String id) {
        return new LearningPathId(UUID.fromString(id));
    }
}
