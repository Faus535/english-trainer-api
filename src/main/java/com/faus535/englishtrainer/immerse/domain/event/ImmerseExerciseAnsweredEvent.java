package com.faus535.englishtrainer.immerse.domain.event;

import com.faus535.englishtrainer.immerse.domain.ImmerseExerciseId;

import java.util.UUID;

public record ImmerseExerciseAnsweredEvent(
        UUID userId,
        ImmerseExerciseId exerciseId,
        String question,
        String correctAnswer,
        String userAnswer
) {}
