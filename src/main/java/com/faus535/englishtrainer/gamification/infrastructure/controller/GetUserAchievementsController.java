package com.faus535.englishtrainer.gamification.infrastructure.controller;

import com.faus535.englishtrainer.gamification.application.GetUserAchievementsUseCase;
import com.faus535.englishtrainer.gamification.domain.UserAchievement;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
class GetUserAchievementsController {

    private final GetUserAchievementsUseCase useCase;

    GetUserAchievementsController(GetUserAchievementsUseCase useCase) {
        this.useCase = useCase;
    }

    record UserAchievementResponse(String id, String userId, String achievementId, String unlockedAt) {}

    @GetMapping("/api/profiles/{userId}/achievements")
    ResponseEntity<List<UserAchievementResponse>> handle(@PathVariable UUID userId) {
        List<UserAchievementResponse> responses = useCase.execute(new UserProfileId(userId)).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    private UserAchievementResponse toResponse(UserAchievement ua) {
        return new UserAchievementResponse(
                ua.id().value().toString(),
                ua.userId().value().toString(),
                ua.achievementId().value(),
                ua.unlockedAt().toString()
        );
    }
}
