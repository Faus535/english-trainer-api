package com.faus535.englishtrainer.talk.infrastructure.controller;

import com.faus535.englishtrainer.talk.application.ListQuickChallengesUseCase;
import com.faus535.englishtrainer.talk.domain.QuickChallenge;
import com.faus535.englishtrainer.talk.domain.QuickChallengeCategory;
import com.faus535.englishtrainer.talk.domain.QuickChallengeDifficulty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
class ListQuickChallengesController {

    private final ListQuickChallengesUseCase useCase;

    ListQuickChallengesController(ListQuickChallengesUseCase useCase) {
        this.useCase = useCase;
    }

    record QuickChallengeResponse(String id, String title, String description,
                                   QuickChallengeDifficulty difficulty, QuickChallengeCategory category) {
        static QuickChallengeResponse from(QuickChallenge challenge) {
            return new QuickChallengeResponse(challenge.id(), challenge.title(), challenge.description(),
                    challenge.difficulty(), challenge.category());
        }
    }

    @GetMapping("/api/talk/quick-challenges")
    ResponseEntity<List<QuickChallengeResponse>> handle() {
        List<QuickChallengeResponse> challenges = useCase.execute(LocalDate.now())
                .stream()
                .map(QuickChallengeResponse::from)
                .toList();
        return ResponseEntity.ok(challenges);
    }
}
