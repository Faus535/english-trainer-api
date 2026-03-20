package com.faus535.englishtrainer.spacedrepetition.infrastructure.controller;

import com.faus535.englishtrainer.spacedrepetition.application.GetReviewStatsUseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
class GetReviewStatsController {

    private final GetReviewStatsUseCase useCase;

    GetReviewStatsController(GetReviewStatsUseCase useCase) {
        this.useCase = useCase;
    }

    record ReviewStatsResponse(int total, int dueToday, int graduated) {}

    @GetMapping("/api/profiles/{userId}/reviews/stats")
    ResponseEntity<ReviewStatsResponse> handle(@PathVariable String userId) {
        GetReviewStatsUseCase.ReviewStats stats = useCase.execute(UserProfileId.fromString(userId));
        return ResponseEntity.ok(new ReviewStatsResponse(stats.total(), stats.dueToday(), stats.graduated()));
    }
}
