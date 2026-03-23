package com.faus535.englishtrainer.pronunciation.infrastructure.controller;

import com.faus535.englishtrainer.pronunciation.application.GetFrequentErrorsUseCase;
import com.faus535.englishtrainer.pronunciation.domain.PronunciationError;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
class GetFrequentErrorsController {

    private final GetFrequentErrorsUseCase useCase;

    GetFrequentErrorsController(GetFrequentErrorsUseCase useCase) {
        this.useCase = useCase;
    }

    record PronunciationErrorResponse(String id, String userId, String word, String expectedPhoneme,
                                       String spokenPhoneme, int occurrenceCount,
                                       Instant lastOccurred, Instant createdAt) {}

    @GetMapping("/api/profiles/{userId}/pronunciation/errors")
    ResponseEntity<List<PronunciationErrorResponse>> handle(
            @PathVariable String userId,
            @RequestParam(defaultValue = "20") int limit) {

        UserProfileId userProfileId = UserProfileId.fromString(userId);
        List<PronunciationError> errors = useCase.execute(userProfileId, limit);

        List<PronunciationErrorResponse> response = errors.stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
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
