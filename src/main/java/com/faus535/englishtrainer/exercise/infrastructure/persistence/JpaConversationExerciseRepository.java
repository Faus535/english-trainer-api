package com.faus535.englishtrainer.exercise.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface JpaConversationExerciseRepository extends JpaRepository<ConversationExerciseEntity, UUID> {
    Optional<ConversationExerciseEntity> findByConversationId(UUID conversationId);
}
