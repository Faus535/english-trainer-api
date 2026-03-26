package com.faus535.englishtrainer.pronunciation.infrastructure.controller;

import com.faus535.englishtrainer.pronunciation.application.RecordPronunciationErrorUseCase;
import com.faus535.englishtrainer.pronunciation.domain.PronunciationError;
import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
class RecordPronunciationErrorController {

    private final RecordPronunciationErrorUseCase useCase;

    RecordPronunciationErrorController(RecordPronunciationErrorUseCase useCase) {
        this.useCase = useCase;
    }

    record RecordPronunciationErrorRequest(String word, String expectedPhoneme, String spokenPhoneme) {}

    record PronunciationErrorResponse(String id, String userId, String word, String expectedPhoneme,
                                       String spokenPhoneme, int occurrenceCount,
                                       Instant lastOccurred, Instant createdAt) {}

    @PostMapping("/api/profiles/{userId}/pronunciation/errors")
    @RequireProfileOwnership
    ResponseEntity<PronunciationErrorResponse> handle(@PathVariable String userId,
                                                       @RequestBody RecordPronunciationErrorRequest request,
                                                       Authentication authentication) {
        UserProfileId userProfileId = UserProfileId.fromString(userId);

        PronunciationError error = useCase.execute(
                userProfileId,
                request.word(),
                request.expectedPhoneme(),
                request.spokenPhoneme()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(error));
    }

    private PronunciationErrorResponse toResponse(PronunciationError error) {
        return new PronunciationErrorResponse(
                error.id().value().toString(),
                error.userId().value().toString(),
                error.word(),
                error.expectedPhoneme(),
                error.spokenPhoneme(),
                error.occurrenceCount(),
                error.lastOccurred(),
                error.createdAt()
        );
    }
}
