package com.faus535.englishtrainer.gamification.infrastructure.controller;

import com.faus535.englishtrainer.gamification.application.GetXpLevelUseCase;
import com.faus535.englishtrainer.gamification.domain.XpLevel;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
class GetXpLevelController {

    private final GetXpLevelUseCase useCase;

    GetXpLevelController(GetXpLevelUseCase useCase) {
        this.useCase = useCase;
    }

    record XpLevelResponse(int level, String name, int currentXp, int xpForCurrentLevel,
                           int xpForNextLevel, double progress) {}

    @GetMapping("/api/profiles/{userId}/xp-level")
    ResponseEntity<XpLevelResponse> handle(@PathVariable UUID userId) throws UserProfileNotFoundException {
        XpLevel xpLevel = useCase.execute(new UserProfileId(userId));
        return ResponseEntity.ok(toResponse(xpLevel));
    }

    private XpLevelResponse toResponse(XpLevel xpLevel) {
        return new XpLevelResponse(
                xpLevel.level(),
                xpLevel.name(),
                xpLevel.currentXp(),
                xpLevel.xpForCurrentLevel(),
                xpLevel.xpForNextLevel(),
                xpLevel.progress()
        );
    }
}
