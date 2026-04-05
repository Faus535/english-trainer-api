package com.faus535.englishtrainer.immerse.domain;

import java.util.UUID;

public record ImmerseExerciseId(UUID value) {

    public ImmerseExerciseId {
        if (value == null) throw new IllegalArgumentException("ImmerseExerciseId cannot be null");
    }

    public static ImmerseExerciseId generate() {
        return new ImmerseExerciseId(UUID.randomUUID());
    }
}
