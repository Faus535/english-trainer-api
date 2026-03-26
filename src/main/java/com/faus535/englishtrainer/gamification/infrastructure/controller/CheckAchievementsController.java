package com.faus535.englishtrainer.gamification.infrastructure.controller;

import com.faus535.englishtrainer.gamification.application.CheckAndUnlockAchievementsUseCase;
import com.faus535.englishtrainer.gamification.domain.Achievement;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
class CheckAchievementsController {

    private final CheckAndUnlockAchievementsUseCase useCase;

    CheckAchievementsController(CheckAndUnlockAchievementsUseCase useCase) {
        this.useCase = useCase;
    }

    record AchievementResponse(String id, String name, String description, String icon, int xpReward) {}

    @PostMapping("/api/profiles/{userId}/achievements/check")
    @RequireProfileOwnership
    ResponseEntity<List<AchievementResponse>> handle(@PathVariable UUID userId,
                                                      Authentication authentication) throws UserProfileNotFoundException {
        List<AchievementResponse> responses = useCase.execute(new UserProfileId(userId)).stream()
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
