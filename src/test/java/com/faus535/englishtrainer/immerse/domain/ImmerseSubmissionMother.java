package com.faus535.englishtrainer.immerse.domain;

import java.time.Instant;
import java.util.UUID;

public final class ImmerseSubmissionMother {

    private static final UUID DEFAULT_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    public static ImmerseSubmission correct(ImmerseExerciseId exerciseId) {
        return ImmerseSubmission.reconstitute(
                ImmerseSubmissionId.generate(),
                exerciseId,
                DEFAULT_USER_ID,
                "A long period without rain",
                true,
                "Correct!",
                Instant.now());
    }

    public static ImmerseSubmission incorrect(ImmerseExerciseId exerciseId) {
        return ImmerseSubmission.reconstitute(
                ImmerseSubmissionId.generate(),
                exerciseId,
                DEFAULT_USER_ID,
                "A type of storm",
                false,
                "The correct answer is: A long period without rain",
                Instant.now());
    }

    public static ImmerseSubmission withUserId(ImmerseExerciseId exerciseId, UUID userId) {
        return ImmerseSubmission.reconstitute(
                ImmerseSubmissionId.generate(),
                exerciseId,
                userId,
                "A long period without rain",
                true,
                "Correct!",
                Instant.now());
    }

    public static ImmerseSubmission forExercise(ImmerseExercise exercise, boolean correct) {
        String answer = correct ? exercise.correctAnswer() : "wrong answer";
        String feedback = correct
                ? "Correct!"
                : "The correct answer is: " + exercise.correctAnswer();
        return ImmerseSubmission.reconstitute(
                ImmerseSubmissionId.generate(),
                exercise.id(),
                DEFAULT_USER_ID,
                answer,
                correct,
                feedback,
                Instant.now());
    }
}
