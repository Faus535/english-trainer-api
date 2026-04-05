package com.faus535.englishtrainer.immerse.domain;

import java.util.List;

public record ImmerseExercise(
        ImmerseExerciseId id,
        ImmerseContentId contentId,
        ExerciseType exerciseType,
        String question,
        String correctAnswer,
        List<String> options,
        int orderIndex
) {

    public ImmerseExercise {
        options = options != null ? List.copyOf(options) : List.of();
    }
}
