package com.faus535.englishtrainer.analytics.infrastructure.controller;

import com.faus535.englishtrainer.analytics.application.GetProgressHistoryUseCase;
import com.faus535.englishtrainer.analytics.domain.LevelHistoryEntry;
import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
class GetProgressHistoryController {

    private final GetProgressHistoryUseCase useCase;

    GetProgressHistoryController(GetProgressHistoryUseCase useCase) {
        this.useCase = useCase;
    }

    record ProgressEntry(String module, String level, Instant changedAt) {}

    @GetMapping("/api/profiles/{userId}/analytics/progress")
    @RequireProfileOwnership
    ResponseEntity<List<ProgressEntry>> handle(@PathVariable UUID userId,
                                                Authentication authentication) {
        List<ProgressEntry> response = useCase.execute(userId).stream()
                .map(e -> new ProgressEntry(e.module(), e.level(), e.changedAt()))
                .toList();
        return ResponseEntity.ok(response);
    }
}
