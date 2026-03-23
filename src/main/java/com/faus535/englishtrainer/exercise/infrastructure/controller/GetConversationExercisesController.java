package com.faus535.englishtrainer.exercise.infrastructure.controller;

import com.faus535.englishtrainer.exercise.application.GetConversationExercisesUseCase;
import com.faus535.englishtrainer.exercise.domain.ConversationExercise;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
class GetConversationExercisesController {

    private final GetConversationExercisesUseCase useCase;

    GetConversationExercisesController(GetConversationExercisesUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/api/conversations/{id}/exercises")
    ResponseEntity<ConversationExercise> handle(@PathVariable UUID id) {
        return useCase.execute(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
