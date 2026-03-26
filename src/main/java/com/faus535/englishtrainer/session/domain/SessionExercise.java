package com.faus535.englishtrainer.session.domain;

import java.util.List;
import java.util.UUID;

public record SessionExercise(
        int exerciseIndex,
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

    public SessionExercise withResult(ExerciseResult result) {
        return new SessionExercise(exerciseIndex, exerciseType, contentIds, targetCount, result);
    }
}
