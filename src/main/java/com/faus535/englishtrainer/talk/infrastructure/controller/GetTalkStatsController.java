package com.faus535.englishtrainer.talk.infrastructure.controller;

import com.faus535.englishtrainer.talk.application.GetTalkStatsUseCase;
import com.faus535.englishtrainer.talk.domain.TalkStats;
import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
class GetTalkStatsController {

    private final GetTalkStatsUseCase useCase;

    GetTalkStatsController(GetTalkStatsUseCase useCase) {
        this.useCase = useCase;
    }

    @RequireProfileOwnership
    @GetMapping("/api/profiles/{userId}/talk/stats")
    ResponseEntity<TalkStats> handle(@PathVariable UUID userId) {
        return ResponseEntity.ok(useCase.execute(userId));
    }
}
