package com.faus535.englishtrainer.reading.infrastructure.controller;

import com.faus535.englishtrainer.reading.application.SubmitReadingAnswersUseCase;
import com.faus535.englishtrainer.shared.domain.error.NotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
class SubmitReadingAnswersController {

    private final SubmitReadingAnswersUseCase useCase;

    SubmitReadingAnswersController(SubmitReadingAnswersUseCase useCase) {
        this.useCase = useCase;
    }

    record SubmitRequest(@NotNull UUID passageId, @NotEmpty List<Integer> answers) {}

    record SubmitResponse(double score, int correctAnswers, int totalQuestions) {}

    @PostMapping("/api/profiles/{userId}/reading/submit")
    ResponseEntity<SubmitResponse> handle(@PathVariable UUID userId,
                                           @Valid @RequestBody SubmitRequest request,
                                           Authentication authentication) throws NotFoundException {

        SubmitReadingAnswersUseCase.SubmitResult result = useCase.execute(
                userId, request.passageId(), request.answers());

        return ResponseEntity.ok(new SubmitResponse(result.score(), result.correctAnswers(), result.totalQuestions()));
    }
}
