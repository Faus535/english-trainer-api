package com.faus535.englishtrainer.exercise.domain;

import java.util.List;

public record Exercise(
        ExerciseType type,
        String instruction,
        String content,
        List<String> options,
        String correctAnswer,
        String explanation,
        String relatedError
) {

    public Exercise {
        options = options != null ? List.copyOf(options) : List.of();
    }
}
