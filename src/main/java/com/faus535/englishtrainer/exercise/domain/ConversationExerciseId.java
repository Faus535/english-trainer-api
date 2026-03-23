package com.faus535.englishtrainer.exercise.domain;

import java.util.UUID;

public record ConversationExerciseId(UUID value) {

    public static ConversationExerciseId generate() {
        return new ConversationExerciseId(UUID.randomUUID());
    }
}
