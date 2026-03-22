package com.faus535.englishtrainer.admin.infrastructure.controller;

import com.faus535.englishtrainer.writing.domain.WritingExercise;
import com.faus535.englishtrainer.writing.domain.WritingExerciseRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasRole('ADMIN')")
class AdminWritingController {

    private final WritingExerciseRepository repository;

    AdminWritingController(WritingExerciseRepository repository) {
        this.repository = repository;
    }

    record CreateExerciseRequest(@NotBlank String prompt, @NotBlank String level, String topic,
                                  Integer minWords, Integer maxWords) {}

    record ExerciseResponse(String id, String prompt, String level, String topic, int minWords, int maxWords) {}

    @PostMapping("/api/admin/writing/exercises")
    ResponseEntity<ExerciseResponse> create(@Valid @RequestBody CreateExerciseRequest request) {
        int minWords = request.minWords() != null ? request.minWords() : 50;
        int maxWords = request.maxWords() != null ? request.maxWords() : 300;

        WritingExercise exercise = WritingExercise.create(request.prompt(), request.level(),
                request.topic(), minWords, maxWords);
        WritingExercise saved = repository.save(exercise);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ExerciseResponse(saved.id().value().toString(), saved.prompt(),
                        saved.level(), saved.topic(), saved.minWords(), saved.maxWords()));
    }
}
