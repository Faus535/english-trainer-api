package com.faus535.englishtrainer.user.infrastructure.controller;

import com.faus535.englishtrainer.user.application.RecordSessionUseCase;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
final class RecordSessionController {

    private final RecordSessionUseCase useCase;

    RecordSessionController(RecordSessionUseCase useCase) {
        this.useCase = useCase;
    }

    record UserProfileResponse(String id, boolean testCompleted, String levelListening, String levelVocabulary,
                               String levelGrammar, String levelPhrases, String levelPronunciation,
                               int sessionCount, int sessionsThisWeek, int xp) {}

    @PostMapping("/api/profiles/{id}/sessions")
    ResponseEntity<UserProfileResponse> handle(@PathVariable UUID id) throws UserProfileNotFoundException {
        UserProfile profile = useCase.execute(new UserProfileId(id));
        return ResponseEntity.ok(toResponse(profile));
    }

    private UserProfileResponse toResponse(UserProfile profile) {
        return new UserProfileResponse(
                profile.id().value().toString(),
                profile.testCompleted(),
                profile.levelListening().value(),
                profile.levelVocabulary().value(),
                profile.levelGrammar().value(),
                profile.levelPhrases().value(),
                profile.levelPronunciation().value(),
                profile.sessionCount(),
                profile.sessionsThisWeek(),
                profile.xp()
        );
    }
}
