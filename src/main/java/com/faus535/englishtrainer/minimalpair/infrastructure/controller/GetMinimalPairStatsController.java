package com.faus535.englishtrainer.minimalpair.infrastructure.controller;

import com.faus535.englishtrainer.minimalpair.application.GetMinimalPairStatsUseCase;
import com.faus535.englishtrainer.minimalpair.domain.MinimalPairResultRepository.CategoryAccuracy;
import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
class GetMinimalPairStatsController {

    private final GetMinimalPairStatsUseCase useCase;

    GetMinimalPairStatsController(GetMinimalPairStatsUseCase useCase) {
        this.useCase = useCase;
    }

    record CategoryStatResponse(String soundCategory, long total, long correct, double accuracy) {}

    record StatsResponse(List<CategoryStatResponse> categories) {}

    @GetMapping("/api/profiles/{userId}/pronunciation/minimal-pairs/stats")
    @RequireProfileOwnership
    ResponseEntity<StatsResponse> handle(@PathVariable UUID userId,
                                          Authentication authentication) {

        List<CategoryStatResponse> stats = useCase.execute(new UserProfileId(userId)).stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(new StatsResponse(stats));
    }

    private CategoryStatResponse toResponse(CategoryAccuracy ca) {
        return new CategoryStatResponse(
                ca.soundCategory(),
                ca.total(),
                ca.correct(),
                ca.accuracy()
        );
    }
}
