package com.faus535.englishtrainer.review.infrastructure.controller;

import com.faus535.englishtrainer.review.application.SubmitReviewResultUseCase;
import com.faus535.englishtrainer.review.domain.ReviewItem;
import com.faus535.englishtrainer.review.domain.error.ReviewItemNotFoundException;
import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
class SubmitReviewResultController {

    private final SubmitReviewResultUseCase useCase;

    SubmitReviewResultController(SubmitReviewResultUseCase useCase) {
        this.useCase = useCase;
    }

    record SubmitResultRequest(@Min(0) @Max(5) int quality) {}
    record ReviewResultResponse(UUID id, String nextReviewAt, int intervalDays,
                                 double easeFactor, int repetitions) {}

    @RequireProfileOwnership
    @PostMapping("/api/profiles/{userId}/review/items/{itemId}/result")
    ResponseEntity<ReviewResultResponse> handle(@PathVariable UUID userId, @PathVariable UUID itemId,
                                                  @Valid @RequestBody SubmitResultRequest request)
            throws ReviewItemNotFoundException {
        ReviewItem updated = useCase.execute(userId, itemId, request.quality());
        return ResponseEntity.ok(new ReviewResultResponse(
                updated.id().value(), updated.nextReviewAt().toString(),
                updated.intervalDays(), updated.easeFactor(), updated.repetitions()));
    }
}
