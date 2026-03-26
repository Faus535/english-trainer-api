package com.faus535.englishtrainer.session.domain;

import java.util.List;
import java.util.UUID;

public record SessionExercise(
        int exerciseIndex,
        int blockIndex,
        String exerciseType,
        List<UUID> contentIds,
        int targetCount,
        ExerciseResult result
) {

    public SessionExercise {
        if (exerciseType == null || exerciseType.isBlank()) {
            throw new IllegalArgumentException("exerciseType cannot be blank");
        }
        contentIds = contentIds != null ? List.copyOf(contentIds) : List.of();
    }

    // Backward-compatible constructor (blockIndex defaults to 0)
    public SessionExercise(int exerciseIndex, String exerciseType, List<UUID> contentIds,
                           int targetCount, ExerciseResult result) {
        this(exerciseIndex, 0, exerciseType, contentIds, targetCount, result);
    }

    public boolean isCompleted() {
        return result != null;
    }

    public SessionExercise withResult(ExerciseResult result) {
        return new SessionExercise(exerciseIndex, blockIndex, exerciseType, contentIds, targetCount, result);
    }
}
