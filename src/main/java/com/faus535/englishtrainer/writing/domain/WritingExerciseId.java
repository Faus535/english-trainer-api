package com.faus535.englishtrainer.writing.domain;

import java.util.UUID;

public record WritingExerciseId(UUID value) {

    public WritingExerciseId {
        if (value == null) throw new IllegalArgumentException("WritingExerciseId cannot be null");
    }

    public static WritingExerciseId generate() { return new WritingExerciseId(UUID.randomUUID()); }
    public static WritingExerciseId fromString(String id) { return new WritingExerciseId(UUID.fromString(id)); }
}
