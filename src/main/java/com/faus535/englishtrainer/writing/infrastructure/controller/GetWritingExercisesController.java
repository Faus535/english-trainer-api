package com.faus535.englishtrainer.writing.infrastructure.controller;

import com.faus535.englishtrainer.writing.application.GetWritingExercisesUseCase;
import com.faus535.englishtrainer.writing.domain.WritingExercise;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class GetWritingExercisesController {

    private final GetWritingExercisesUseCase useCase;

    GetWritingExercisesController(GetWritingExercisesUseCase useCase) {
        this.useCase = useCase;
    }

    record ExerciseResponse(String id, String prompt, String level, String topic, int minWords, int maxWords) {}

    @GetMapping("/api/writing/exercises")
    ResponseEntity<List<ExerciseResponse>> handle(@RequestParam String level) {
        List<ExerciseResponse> response = useCase.execute(level).stream()
                .map(e -> new ExerciseResponse(e.id().value().toString(), e.prompt(), e.level(),
                        e.topic(), e.minWords(), e.maxWords()))
                .toList();
        return ResponseEntity.ok(response);
    }
}
