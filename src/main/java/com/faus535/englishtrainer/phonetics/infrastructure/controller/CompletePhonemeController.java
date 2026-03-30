package com.faus535.englishtrainer.phonetics.infrastructure.controller;

import com.faus535.englishtrainer.phonetics.application.CompletePhonemeUseCase;
import com.faus535.englishtrainer.phonetics.application.CompletePhonemeUseCase.CompletionResult;
import com.faus535.englishtrainer.phonetics.domain.PhonemeId;
import com.faus535.englishtrainer.phonetics.domain.error.*;
import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
class CompletePhonemeController {
    private final CompletePhonemeUseCase useCase;

    CompletePhonemeController(CompletePhonemeUseCase useCase) {
        this.useCase = useCase;
    }

    record CompletionResponse(boolean completed, String completedAt) {}

    @PutMapping("/api/profiles/{userId}/phonetics/phonemes/{phonemeId}/complete")
    @RequireProfileOwnership
    ResponseEntity<CompletionResponse> handle(@PathVariable UUID userId,
                                               @PathVariable UUID phonemeId,
                                               Authentication authentication)
            throws PhonemeNotFoundException, PhonemeAlreadyCompletedException,
                   InsufficientPhrasesCompletedException {
        CompletionResult result = useCase.execute(
            new UserProfileId(userId), new PhonemeId(phonemeId)
        );
        return ResponseEntity.ok(new CompletionResponse(
            result.completed(), result.completedAt().toString()
        ));
    }
}
