package com.faus535.englishtrainer.exercise.domain;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ConversationExercise(
        ConversationExerciseId id,
        UUID conversationId,
        UUID userId,
        List<Exercise> exercises,
        Instant createdAt
) {

    public ConversationExercise {
        exercises = exercises != null ? List.copyOf(exercises) : List.of();
    }

    public static ConversationExercise create(UUID conversationId, UUID userId, List<Exercise> exercises) {
        return new ConversationExercise(
                ConversationExerciseId.generate(), conversationId, userId, exercises, Instant.now());
    }

    public static ConversationExercise reconstitute(ConversationExerciseId id, UUID conversationId,
                                                      UUID userId, List<Exercise> exercises, Instant createdAt) {
        return new ConversationExercise(id, conversationId, userId, exercises, createdAt);
    }
}
