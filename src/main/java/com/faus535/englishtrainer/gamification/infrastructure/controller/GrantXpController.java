package com.faus535.englishtrainer.gamification.infrastructure.controller;

import com.faus535.englishtrainer.gamification.application.GetXpLevelUseCase;
import com.faus535.englishtrainer.gamification.application.GrantXpUseCase;
import com.faus535.englishtrainer.gamification.domain.XpLevel;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.error.InvalidXpAmountException;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
class GrantXpController {

    private final GrantXpUseCase grantXpUseCase;
    private final GetXpLevelUseCase getXpLevelUseCase;

    GrantXpController(GrantXpUseCase grantXpUseCase, GetXpLevelUseCase getXpLevelUseCase) {
        this.grantXpUseCase = grantXpUseCase;
        this.getXpLevelUseCase = getXpLevelUseCase;
    }

    record GrantXpRequest(@NotNull @Min(1) Integer amount, String reason) {}

    record XpLevelResponse(int level, String name, int currentXp, int xpForCurrentLevel,
                           int xpForNextLevel, double progress) {}

    record GrantXpResponse(int xpGranted, int totalXp, XpLevelResponse level) {}

    @PostMapping("/api/profiles/{userId}/xp")
    ResponseEntity<GrantXpResponse> handle(@PathVariable UUID userId,
                                            @Valid @RequestBody GrantXpRequest request) throws UserProfileNotFoundException, InvalidXpAmountException {
        UserProfileId profileId = new UserProfileId(userId);
        GrantXpUseCase.XpGrantResult result = grantXpUseCase.execute(profileId, request.amount(), request.reason());
        XpLevel xpLevel = getXpLevelUseCase.execute(profileId);

        return ResponseEntity.ok(new GrantXpResponse(
                result.xpGranted(),
                result.totalXp(),
                new XpLevelResponse(
                        xpLevel.level(),
                        xpLevel.name(),
                        xpLevel.currentXp(),
                        xpLevel.xpForCurrentLevel(),
                        xpLevel.xpForNextLevel(),
                        xpLevel.progress()
                )
        ));
    }
}
