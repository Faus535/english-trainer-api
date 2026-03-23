package com.faus535.englishtrainer.exercise.application;

import com.faus535.englishtrainer.exercise.domain.ConversationExercise;
import com.faus535.englishtrainer.exercise.domain.ConversationExerciseRepository;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@UseCase
public class GetConversationExercisesUseCase {

    private final ConversationExerciseRepository repository;

    public GetConversationExercisesUseCase(ConversationExerciseRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Optional<ConversationExercise> execute(UUID conversationId) {
        return repository.findByConversationId(conversationId);
    }
}
