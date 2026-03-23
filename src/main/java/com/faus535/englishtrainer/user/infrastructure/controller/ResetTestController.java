package com.faus535.englishtrainer.user.infrastructure.controller;

import com.faus535.englishtrainer.user.application.ResetTestUseCase;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
class ResetTestController {

    private final ResetTestUseCase useCase;

    ResetTestController(ResetTestUseCase useCase) {
        this.useCase = useCase;
    }

    record ResetTestResponse(String id, boolean testCompleted, String levelListening, String levelVocabulary,
                             String levelGrammar, String levelPhrases, String levelPronunciation) {}

    @PutMapping("/api/profiles/{id}/reset-test")
    ResponseEntity<ResetTestResponse> handle(@PathVariable UUID id) throws UserProfileNotFoundException {
        UserProfile profile = useCase.execute(new UserProfileId(id));
        return ResponseEntity.ok(new ResetTestResponse(
                profile.id().value().toString(),
                profile.testCompleted(),
                profile.levelListening().value(),
                profile.levelVocabulary().value(),
                profile.levelGrammar().value(),
                profile.levelPhrases().value(),
                profile.levelPronunciation().value()
        ));
    }
}
