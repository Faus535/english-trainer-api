package com.faus535.englishtrainer.immerse.domain;

import java.util.List;

public final class ImmerseExerciseMother {

    public static ImmerseExercise multipleChoice(ImmerseContentId contentId) {
        return new ImmerseExercise(
                ImmerseExerciseId.generate(), contentId,
                ExerciseType.MULTIPLE_CHOICE, "What does 'drought' mean?",
                "A long period without rain",
                List.of("A long period without rain", "A type of storm", "A flood"),
                0);
    }

    public static ImmerseExercise fillTheGap(ImmerseContentId contentId) {
        return new ImmerseExercise(
                ImmerseExerciseId.generate(), contentId,
                ExerciseType.FILL_THE_GAP, "Carbon ___ are rising rapidly.",
                "emissions", List.of(), 1);
    }

    public static ImmerseExercise trueFalse(ImmerseContentId contentId) {
        return new ImmerseExercise(
                ImmerseExerciseId.generate(), contentId,
                ExerciseType.TRUE_FALSE, "Climate change only affects cold countries.",
                "False", List.of("True", "False"), 2);
    }
}
