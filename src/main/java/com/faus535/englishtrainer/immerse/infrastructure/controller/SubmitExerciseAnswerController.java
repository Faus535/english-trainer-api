package com.faus535.englishtrainer.immerse.infrastructure.controller;

import com.faus535.englishtrainer.immerse.application.SubmitExerciseAnswerUseCase;
import com.faus535.englishtrainer.immerse.domain.error.ImmerseExerciseNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
class SubmitExerciseAnswerController {

    private final SubmitExerciseAnswerUseCase useCase;

    SubmitExerciseAnswerController(SubmitExerciseAnswerUseCase useCase) { this.useCase = useCase; }

    record SubmitAnswerRequest(@NotBlank String answer) {}
    record AnswerResponse(boolean correct, String correctAnswer, String feedback) {}

    @PostMapping("/api/immerse/content/{contentId}/exercises/{exerciseId}/submit")
    ResponseEntity<AnswerResponse> handle(@PathVariable UUID contentId, @PathVariable UUID exerciseId,
                                            @Valid @RequestBody SubmitAnswerRequest request,
                                            Authentication authentication) throws ImmerseExerciseNotFoundException {
        UUID userId = UUID.fromString(authentication.getName());
        var result = useCase.execute(userId, exerciseId, request.answer());
        return ResponseEntity.ok(new AnswerResponse(result.correct(), result.correctAnswer(), result.feedback()));
    }
}
