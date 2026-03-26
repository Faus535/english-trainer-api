package com.faus535.englishtrainer.analytics.infrastructure.controller;

import com.faus535.englishtrainer.analytics.application.GetAnalyticsSummaryUseCase;
import com.faus535.englishtrainer.analytics.domain.AnalyticsSummary;
import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
class GetAnalyticsSummaryController {

    private final GetAnalyticsSummaryUseCase useCase;

    GetAnalyticsSummaryController(GetAnalyticsSummaryUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/api/profiles/{userId}/analytics/summary")
    @RequireProfileOwnership
    ResponseEntity<AnalyticsSummary> handle(@PathVariable UUID userId,
                                             Authentication authentication) {
        return ResponseEntity.ok(useCase.execute(userId));
    }
}
