package com.faus535.englishtrainer.user.infrastructure.controller;

import com.faus535.englishtrainer.user.application.AddXpUseCase;
import com.faus535.englishtrainer.user.domain.UserProfile;
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
final class AddXpController {

    private final AddXpUseCase useCase;

    AddXpController(AddXpUseCase useCase) {
        this.useCase = useCase;
    }

    record AddXpRequest(@NotNull @Min(1) Integer amount) {}

    @PostMapping("/api/profiles/{id}/xp")
    ResponseEntity<UserProfileResponse> handle(@PathVariable UUID id,
                                               @Valid @RequestBody AddXpRequest request) throws UserProfileNotFoundException, InvalidXpAmountException {
        UserProfile profile = useCase.execute(new UserProfileId(id), request.amount());
        return ResponseEntity.ok(toResponse(profile));
    }

    record UserProfileResponse(String id, boolean testCompleted, String levelListening, String levelVocabulary,
                               String levelGrammar, String levelPhrases, String levelPronunciation,
                               int sessionCount, int sessionsThisWeek, int xp) {}

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
