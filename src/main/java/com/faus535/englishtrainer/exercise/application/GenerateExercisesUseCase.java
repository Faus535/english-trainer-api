package com.faus535.englishtrainer.exercise.application;

import com.faus535.englishtrainer.exercise.domain.*;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@UseCase
public class GenerateExercisesUseCase {

    private static final Logger log = LoggerFactory.getLogger(GenerateExercisesUseCase.class);

    private final ExerciseGeneratorPort generatorPort;
    private final ConversationExerciseRepository repository;

    public GenerateExercisesUseCase(ExerciseGeneratorPort generatorPort,
                                     ConversationExerciseRepository repository) {
        this.generatorPort = generatorPort;
        this.repository = repository;
    }

    @Transactional
    public ConversationExercise execute(UUID conversationId, UUID userId, String level, List<String> errors) {
        if (errors == null || errors.isEmpty()) return null;

        try {
            List<Exercise> exercises = generatorPort.generate(level, errors);
            if (exercises.isEmpty()) return null;

            ConversationExercise conversationExercise = ConversationExercise.create(conversationId, userId, exercises);
            return repository.save(conversationExercise);
        } catch (Exception e) {
            log.error("Failed to generate exercises for conversation {}: {}", conversationId, e.getMessage(), e);
            return null;
        }
    }
}
