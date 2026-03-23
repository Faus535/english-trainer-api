package com.faus535.englishtrainer.minigame.infrastructure.controller;

import com.faus535.englishtrainer.minigame.application.SaveMiniGameScoreUseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
class SaveMiniGameScoreController {

    private final SaveMiniGameScoreUseCase useCase;

    SaveMiniGameScoreController(SaveMiniGameScoreUseCase useCase) {
        this.useCase = useCase;
    }

    record SaveScoreRequest(@NotBlank String gameType, @NotNull @Min(0) Integer score) {}

    record SaveScoreResponse(int xpEarned, int score, String gameType) {}

    @PostMapping("/api/profiles/{userId}/minigames/scores")
    ResponseEntity<SaveScoreResponse> handle(@PathVariable UUID userId,
                                              @Valid @RequestBody SaveScoreRequest request) {
        UserProfileId profileId = new UserProfileId(userId);
        SaveMiniGameScoreUseCase.SaveScoreResult result = useCase.execute(profileId, request.gameType(), request.score());
        return ResponseEntity.ok(new SaveScoreResponse(result.xpEarned(), result.score(), result.gameType()));
    }
}
