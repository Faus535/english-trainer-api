package com.faus535.englishtrainer.gamification.infrastructure.controller;

import com.faus535.englishtrainer.gamification.application.GetAllAchievementsUseCase;
import com.faus535.englishtrainer.gamification.domain.Achievement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class GetAllAchievementsController {

    private final GetAllAchievementsUseCase useCase;

    GetAllAchievementsController(GetAllAchievementsUseCase useCase) {
        this.useCase = useCase;
    }

    record AchievementResponse(String id, String name, String description, String icon, int xpReward) {}

    @GetMapping("/api/achievements")
    ResponseEntity<List<AchievementResponse>> handle() {
        List<AchievementResponse> responses = useCase.execute().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    private AchievementResponse toResponse(Achievement achievement) {
        return new AchievementResponse(
                achievement.id().value(),
                achievement.name(),
                achievement.description(),
                achievement.icon(),
                achievement.xpReward()
        );
    }
}
