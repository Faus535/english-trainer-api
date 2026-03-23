package com.faus535.englishtrainer.dailychallenge.infrastructure.controller;

import com.faus535.englishtrainer.dailychallenge.application.GetUserChallengeProgressUseCase;
import com.faus535.englishtrainer.dailychallenge.domain.DailyChallenge;
import com.faus535.englishtrainer.dailychallenge.domain.error.ChallengeNotFoundException;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
class GetUserChallengeProgressController {

    private final GetUserChallengeProgressUseCase useCase;

    GetUserChallengeProgressController(GetUserChallengeProgressUseCase useCase) {
        this.useCase = useCase;
    }

    record ProgressResponse(String challengeId, String challengeType, String descriptionEs, String descriptionEn,
                             int target, int xpReward, int progress, boolean completed) {}

    @GetMapping("/api/profiles/{userId}/challenges/today")
    ResponseEntity<ProgressResponse> handle(@PathVariable String userId) throws ChallengeNotFoundException {
        UserProfileId profileId = UserProfileId.fromString(userId);
        GetUserChallengeProgressUseCase.UserChallengeProgress result = useCase.execute(profileId);
        return ResponseEntity.ok(toResponse(result));
    }

    private ProgressResponse toResponse(GetUserChallengeProgressUseCase.UserChallengeProgress result) {
        DailyChallenge challenge = result.challenge();
        return new ProgressResponse(
                challenge.id().value().toString(),
                challenge.challengeType().name(),
                challenge.descriptionEs(),
                challenge.descriptionEn(),
                challenge.target(),
                challenge.xpReward(),
                result.progress(),
                result.completed()
        );
    }
}
