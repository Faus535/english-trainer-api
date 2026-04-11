package com.faus535.englishtrainer.immerse.domain;

import org.springframework.lang.Nullable;

import java.util.List;

public record ImmerseExercise(
        ImmerseExerciseId id,
        ImmerseContentId contentId,
        ExerciseType exerciseType,
        String question,
        String correctAnswer,
        List<String> options,
        int orderIndex,
        @Nullable String listenText,
        @Nullable Integer blankPosition
) {

    public ImmerseExercise {
        options = options != null ? List.copyOf(options) : List.of();
    }
}
