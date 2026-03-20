package com.faus535.englishtrainer.user.infrastructure.controller;

import com.faus535.englishtrainer.user.application.CreateUserProfileUseCase;
import com.faus535.englishtrainer.user.domain.UserProfile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class CreateUserProfileController {

    private final CreateUserProfileUseCase useCase;

    CreateUserProfileController(CreateUserProfileUseCase useCase) {
        this.useCase = useCase;
    }

    record UserProfileResponse(String id, boolean testCompleted, String levelListening, String levelVocabulary,
                               String levelGrammar, String levelPhrases, String levelPronunciation,
                               int sessionCount, int sessionsThisWeek, int xp) {}

    @PostMapping("/api/profiles")
    ResponseEntity<UserProfileResponse> handle() {
        UserProfile profile = useCase.execute();
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(profile));
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
