package com.faus535.englishtrainer.exercise.domain;

import java.util.Optional;
import java.util.UUID;

public interface ConversationExerciseRepository {
    ConversationExercise save(ConversationExercise exercise);
    Optional<ConversationExercise> findByConversationId(UUID conversationId);
}
