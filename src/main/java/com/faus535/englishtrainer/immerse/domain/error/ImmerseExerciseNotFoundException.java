package com.faus535.englishtrainer.immerse.domain.error;

import com.faus535.englishtrainer.immerse.domain.ImmerseExerciseId;

public final class ImmerseExerciseNotFoundException extends ImmerseException {
    public ImmerseExerciseNotFoundException(ImmerseExerciseId id) {
        super("Immerse exercise not found: " + id.value());
    }
}
