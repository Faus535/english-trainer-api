package com.faus535.englishtrainer.immerse.infrastructure.controller;

import com.faus535.englishtrainer.immerse.application.GetImmerseExercisesUseCase;
import com.faus535.englishtrainer.immerse.domain.ImmerseExercise;
import com.faus535.englishtrainer.immerse.domain.error.ImmerseContentNotFoundException;
import com.faus535.englishtrainer.immerse.domain.error.ImmerseContentNotProcessedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
class GetImmerseExercisesController {

    private final GetImmerseExercisesUseCase useCase;

    GetImmerseExercisesController(GetImmerseExercisesUseCase useCase) { this.useCase = useCase; }

    record ExerciseResponse(String id, String exerciseType, String question, List<String> options, int orderIndex) {}

    @GetMapping("/api/immerse/content/{id}/exercises")
    ResponseEntity<List<ExerciseResponse>> handle(@PathVariable UUID id)
            throws ImmerseContentNotFoundException, ImmerseContentNotProcessedException {
        List<ExerciseResponse> exercises = useCase.execute(id).stream()
                .map(e -> new ExerciseResponse(e.id().value().toString(), e.exerciseType().name(),
                        e.question(), e.options(), e.orderIndex()))
                .toList();
        return ResponseEntity.ok(exercises);
    }
}
