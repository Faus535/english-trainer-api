package com.faus535.englishtrainer.writing.infrastructure.controller;

import com.faus535.englishtrainer.writing.application.GetWritingHistoryUseCase;
import com.faus535.englishtrainer.writing.domain.WritingFeedback;
import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import com.faus535.englishtrainer.writing.domain.WritingSubmission;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
class GetWritingHistoryController {

    private final GetWritingHistoryUseCase useCase;

    GetWritingHistoryController(GetWritingHistoryUseCase useCase) {
        this.useCase = useCase;
    }

    record HistoryResponse(String id, String exerciseId, String text, int wordCount,
                           WritingFeedback feedback, Instant submittedAt) {}

    @GetMapping("/api/profiles/{userId}/writing/history")
    @RequireProfileOwnership
    ResponseEntity<List<HistoryResponse>> handle(@PathVariable UUID userId,
                                                   Authentication authentication) {
        List<HistoryResponse> response = useCase.execute(userId).stream()
                .map(s -> new HistoryResponse(s.id().toString(), s.exerciseId().value().toString(),
                        s.text(), s.wordCount(), s.feedback(), s.submittedAt()))
                .toList();
        return ResponseEntity.ok(response);
    }
}
