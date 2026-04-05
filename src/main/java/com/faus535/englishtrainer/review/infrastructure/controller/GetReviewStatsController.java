package com.faus535.englishtrainer.review.infrastructure.controller;

import com.faus535.englishtrainer.review.application.GetReviewStatsUseCase;
import com.faus535.englishtrainer.review.domain.ReviewStats;
import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
class GetReviewStatsController {

    private final GetReviewStatsUseCase useCase;

    GetReviewStatsController(GetReviewStatsUseCase useCase) {
        this.useCase = useCase;
    }

    @RequireProfileOwnership
    @GetMapping("/api/profiles/{userId}/review/stats")
    ResponseEntity<ReviewStats> handle(@PathVariable UUID userId) {
        return ResponseEntity.ok(useCase.execute(userId));
    }
}
