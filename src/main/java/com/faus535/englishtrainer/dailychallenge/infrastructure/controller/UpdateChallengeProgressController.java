package com.faus535.englishtrainer.dailychallenge.infrastructure.controller;

import com.faus535.englishtrainer.dailychallenge.application.UpdateChallengeProgressUseCase;
import com.faus535.englishtrainer.dailychallenge.domain.error.ChallengeNotFoundException;
import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class UpdateChallengeProgressController {

    private final UpdateChallengeProgressUseCase useCase;

    UpdateChallengeProgressController(UpdateChallengeProgressUseCase useCase) {
        this.useCase = useCase;
    }

    record UpdateProgressRequest(int progress) {}

    record ProgressResultResponse(int progress, int target, boolean completed, int xpReward) {}

    @PutMapping("/api/profiles/{userId}/challenges/today/progress")
    @RequireProfileOwnership
    ResponseEntity<ProgressResultResponse> handle(@PathVariable String userId,
                                                   @RequestBody UpdateProgressRequest request,
                                                   Authentication authentication) throws ChallengeNotFoundException {
        UserProfileId profileId = UserProfileId.fromString(userId);
        UpdateChallengeProgressUseCase.ProgressResult result = useCase.execute(profileId, request.progress());
        return ResponseEntity.ok(new ProgressResultResponse(
                result.progress(),
                result.target(),
                result.completed(),
                result.xpReward()
        ));
    }
}
