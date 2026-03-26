package com.faus535.englishtrainer.activity.infrastructure.controller;

import com.faus535.englishtrainer.activity.application.GetStreakUseCase;
import com.faus535.englishtrainer.activity.domain.StreakInfo;
import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
class GetStreakController {

    private final GetStreakUseCase useCase;

    GetStreakController(GetStreakUseCase useCase) {
        this.useCase = useCase;
    }

    record StreakResponse(int currentStreak, int bestStreak, String lastActiveDate) {}

    @GetMapping("/api/profiles/{userId}/streak")
    @RequireProfileOwnership
    ResponseEntity<StreakResponse> handle(@PathVariable String userId,
                                          Authentication authentication) {
        UserProfileId userProfileId = UserProfileId.fromString(userId);
        StreakInfo streakInfo = useCase.execute(userProfileId);

        return ResponseEntity.ok(toResponse(streakInfo));
    }

    private StreakResponse toResponse(StreakInfo streakInfo) {
        return new StreakResponse(
                streakInfo.currentStreak(),
                streakInfo.bestStreak(),
                streakInfo.lastActiveDate() != null ? streakInfo.lastActiveDate().toString() : null
        );
    }
}
