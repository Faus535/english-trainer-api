package com.faus535.englishtrainer.tutorerror.infrastructure.controller;

import com.faus535.englishtrainer.tutorerror.application.GenerateErrorExerciseUseCase;
import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import com.faus535.englishtrainer.tutorerror.domain.TutorErrorId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class GenerateErrorExerciseController {

    private final GenerateErrorExerciseUseCase useCase;

    GenerateErrorExerciseController(GenerateErrorExerciseUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping("/api/profiles/{userId}/tutor/errors/{errorId}/exercise")
    @RequireProfileOwnership
    ResponseEntity<String> handle(
            @PathVariable String userId,
            @PathVariable String errorId,
            Authentication authentication) throws Exception {

        TutorErrorId tutorErrorId = TutorErrorId.fromString(errorId);
        String exerciseJson = useCase.execute(tutorErrorId);

        return ResponseEntity.ok(exerciseJson);
    }
}
