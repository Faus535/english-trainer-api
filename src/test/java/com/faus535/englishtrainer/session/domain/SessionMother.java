package com.faus535.englishtrainer.session.domain;

import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.util.List;

public final class SessionMother {

    private SessionMother() {}

    public static Session create() {
        return Session.create(
                UserProfileId.generate(),
                new SessionMode("full"),
                new SessionType("normal"),
                "listening",
                "vocabulary",
                null,
                List.of(
                        new SessionBlock("warmup", "review", 3),
                        new SessionBlock("listening", "listening", 7),
                        new SessionBlock("secondary", "vocabulary", 7),
                        new SessionBlock("practice", "mixed", 4)
                )
        );
    }

    public static Session create(UserProfileId userId) {
        return Session.create(
                userId,
                new SessionMode("full"),
                new SessionType("normal"),
                "listening",
                "vocabulary",
                null,
                List.of(
                        new SessionBlock("warmup", "review", 3),
                        new SessionBlock("listening", "listening", 7),
                        new SessionBlock("secondary", "vocabulary", 7),
                        new SessionBlock("practice", "mixed", 4)
                )
        );
    }

    public static Session createWithExercises(UserProfileId userId, List<SessionExercise> exercises) {
        return Session.create(
                userId,
                new SessionMode("full"),
                new SessionType("normal"),
                "listening",
                "vocabulary",
                null,
                List.of(
                        new SessionBlock("warmup", "review", 3, 3, List.of()),
                        new SessionBlock("listening", "listening", 7, 3, List.of()),
                        new SessionBlock("secondary", "vocabulary", 7, 3, List.of()),
                        new SessionBlock("practice", "mixed", 4, 3, List.of())
                ),
                exercises
        );
    }

    public static Session createWithCompletedBlock(int blockIndex) {
        UserProfileId userId = UserProfileId.generate();
        ExerciseResult doneResult = new ExerciseResult(3, 5, 1200L, java.time.Instant.now());
        List<SessionExercise> exercises = List.of(
                new SessionExercise(0, 0, "review", List.of(), 3, blockIndex == 0 ? doneResult : null),
                new SessionExercise(1, 0, "review", List.of(), 3, blockIndex == 0 ? doneResult : null),
                new SessionExercise(2, 1, "listening", List.of(), 3, blockIndex == 1 ? doneResult : null),
                new SessionExercise(3, 1, "listening", List.of(), 3, blockIndex == 1 ? doneResult : null)
        );
        return createWithExercises(userId, exercises);
    }

    public static Session createFullyCompleted() {
        UserProfileId userId = UserProfileId.generate();
        ExerciseResult doneResult = new ExerciseResult(3, 5, 1200L, java.time.Instant.now());
        List<SessionExercise> exercises = List.of(
                new SessionExercise(0, 0, "review", List.of(), 3, doneResult),
                new SessionExercise(1, 0, "review", List.of(), 3, doneResult),
                new SessionExercise(2, 1, "listening", List.of(), 3, doneResult),
                new SessionExercise(3, 1, "listening", List.of(), 3, doneResult)
        );
        return createWithExercises(userId, exercises);
    }
}
