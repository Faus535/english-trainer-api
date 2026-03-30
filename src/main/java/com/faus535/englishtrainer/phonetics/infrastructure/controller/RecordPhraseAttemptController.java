package com.faus535.englishtrainer.phonetics.infrastructure.controller;

import com.faus535.englishtrainer.phonetics.application.RecordPhraseAttemptUseCase;
import com.faus535.englishtrainer.phonetics.application.RecordPhraseAttemptUseCase.AttemptResult;
import com.faus535.englishtrainer.phonetics.domain.PhonemeId;
import com.faus535.englishtrainer.phonetics.domain.PhonemePracticePhraseId;
import com.faus535.englishtrainer.phonetics.domain.error.PhonemeNotFoundException;
import com.faus535.englishtrainer.phonetics.domain.error.PhraseNotFoundException;
import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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

    record AttemptRequest(@NotNull @Min(0) @Max(100) Integer score) {}

    record AttemptResponse(int attemptsCount, int correctAttemptsCount,
                           boolean phraseCompleted, boolean phonemeCompleted,
                           int phrasesCompleted, int phrasesTotal) {}

    @PostMapping("/api/profiles/{userId}/phonetics/phonemes/{phonemeId}/phrases/{phraseId}/attempt")
    @RequireProfileOwnership
    ResponseEntity<AttemptResponse> handle(@PathVariable UUID userId,
                                            @PathVariable UUID phonemeId,
                                            @PathVariable UUID phraseId,
                                            @Valid @RequestBody AttemptRequest request,
                                            Authentication authentication)
            throws PhonemeNotFoundException, PhraseNotFoundException {
        AttemptResult result = useCase.execute(
            new UserProfileId(userId), new PhonemeId(phonemeId),
            new PhonemePracticePhraseId(phraseId), request.score()
        );
        return ResponseEntity.ok(new AttemptResponse(
            result.attemptsCount(), result.correctAttemptsCount(),
            result.phraseCompleted(), result.phonemeCompleted(),
            result.phrasesCompleted(), result.phrasesTotal()
        ));
    }
}
