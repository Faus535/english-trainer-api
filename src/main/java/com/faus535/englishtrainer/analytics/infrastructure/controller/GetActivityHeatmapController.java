package com.faus535.englishtrainer.analytics.infrastructure.controller;

import com.faus535.englishtrainer.analytics.application.GetActivityHeatmapUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
class GetActivityHeatmapController {

    private final GetActivityHeatmapUseCase useCase;

    GetActivityHeatmapController(GetActivityHeatmapUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/api/profiles/{userId}/analytics/activity-heatmap")
    ResponseEntity<Map<String, Boolean>> handle(@PathVariable UUID userId) {
        return ResponseEntity.ok(useCase.execute(userId));
    }
}
