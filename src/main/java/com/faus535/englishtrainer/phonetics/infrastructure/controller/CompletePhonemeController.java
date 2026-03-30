package com.faus535.englishtrainer.phonetics.infrastructure.controller;

import com.faus535.englishtrainer.phonetics.application.CompletePhonemeUseCase;
import com.faus535.englishtrainer.phonetics.domain.PhonemeId;
import com.faus535.englishtrainer.phonetics.domain.error.InsufficientPhrasesCompletedException;
import com.faus535.englishtrainer.phonetics.domain.error.PhonemeAlreadyCompletedException;
import com.faus535.englishtrainer.phonetics.domain.error.PhonemeNotFoundException;
import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
class CompletePhonemeController {

    private final CompletePhonemeUseCase useCase;

    CompletePhonemeController(CompletePhonemeUseCase useCase) {
        this.useCase = useCase;
    }

    @PutMapping("/api/profiles/{userId}/phonetics/phonemes/{phonemeId}/complete")
    @RequireProfileOwnership
    ResponseEntity<Void> handle(@PathVariable UUID userId,
                                 @PathVariable UUID phonemeId,
                                 Authentication authentication)
            throws PhonemeNotFoundException, PhonemeAlreadyCompletedException,
                   InsufficientPhrasesCompletedException {
        useCase.execute(new UserProfileId(userId), new PhonemeId(phonemeId));
        return ResponseEntity.noContent().build();
    }
}
