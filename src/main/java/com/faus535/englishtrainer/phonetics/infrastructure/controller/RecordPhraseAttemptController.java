package com.faus535.englishtrainer.phonetics.infrastructure.controller;

import com.faus535.englishtrainer.phonetics.application.RecordPhraseAttemptUseCase;
import com.faus535.englishtrainer.phonetics.domain.PhonemeId;
import com.faus535.englishtrainer.phonetics.domain.PhonemePracticePhraseId;
import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
class RecordPhraseAttemptController {

    private final RecordPhraseAttemptUseCase useCase;

    RecordPhraseAttemptController(RecordPhraseAttemptUseCase useCase) {
        this.useCase = useCase;
    }

    record AttemptRequest(@NotNull UUID phonemeId, @NotNull UUID phraseId, boolean correct) {}

    record AttemptResponse(boolean correct, boolean alreadyRecorded) {}

    @PostMapping("/api/profiles/{userId}/phonetics/attempts")
    @RequireProfileOwnership
    ResponseEntity<AttemptResponse> handle(@PathVariable UUID userId,
                                            @Valid @RequestBody AttemptRequest request,
                                            Authentication authentication) {
        var result = useCase.execute(
                new UserProfileId(userId),
                new PhonemeId(request.phonemeId()),
                new PhonemePracticePhraseId(request.phraseId()),
                request.correct()
        );
        return ResponseEntity.ok(new AttemptResponse(result.correct(), result.alreadyRecorded()));
    }
}
