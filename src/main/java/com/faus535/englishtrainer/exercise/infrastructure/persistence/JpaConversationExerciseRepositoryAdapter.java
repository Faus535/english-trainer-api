package com.faus535.englishtrainer.exercise.infrastructure.persistence;

import com.faus535.englishtrainer.exercise.domain.ConversationExercise;
import com.faus535.englishtrainer.exercise.domain.ConversationExerciseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
class JpaConversationExerciseRepositoryAdapter implements ConversationExerciseRepository {

    private final JpaConversationExerciseRepository jpaRepository;

    JpaConversationExerciseRepositoryAdapter(JpaConversationExerciseRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ConversationExercise save(ConversationExercise exercise) {
        ConversationExerciseEntity entity = ConversationExerciseEntity.fromAggregate(exercise);
        return jpaRepository.save(entity).toAggregate();
    }

    @Override
    public Optional<ConversationExercise> findByConversationId(UUID conversationId) {
        return jpaRepository.findByConversationId(conversationId).map(ConversationExerciseEntity::toAggregate);
    }
}
