package com.faus535.englishtrainer.dailychallenge.infrastructure.controller;

import com.faus535.englishtrainer.dailychallenge.application.GetTodayChallengeUseCase;
import com.faus535.englishtrainer.dailychallenge.domain.DailyChallenge;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class GetTodayChallengeController {

    private final GetTodayChallengeUseCase useCase;

    GetTodayChallengeController(GetTodayChallengeUseCase useCase) {
        this.useCase = useCase;
    }

    record ChallengeResponse(String id, String challengeType, String descriptionEs, String descriptionEn,
                              int target, int xpReward, String challengeDate) {}

    @GetMapping("/api/challenges/today")
    ResponseEntity<ChallengeResponse> handle() {
        DailyChallenge challenge = useCase.execute();
        return ResponseEntity.ok(toResponse(challenge));
    }

    private ChallengeResponse toResponse(DailyChallenge challenge) {
        return new ChallengeResponse(
                challenge.id().value().toString(),
                challenge.challengeType().name(),
                challenge.descriptionEs(),
                challenge.descriptionEn(),
                challenge.target(),
                challenge.xpReward(),
                challenge.challengeDate().toString()
        );
    }
}
