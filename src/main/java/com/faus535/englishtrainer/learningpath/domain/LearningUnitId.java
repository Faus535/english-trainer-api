package com.faus535.englishtrainer.learningpath.domain;

import java.util.UUID;

public record LearningUnitId(UUID value) {

    public LearningUnitId {
        if (value == null) {
            throw new IllegalArgumentException("LearningUnitId cannot be null");
        }
    }

    public static LearningUnitId generate() {
        return new LearningUnitId(UUID.randomUUID());
    }

    public static LearningUnitId fromString(String id) {
        return new LearningUnitId(UUID.fromString(id));
    }
}
