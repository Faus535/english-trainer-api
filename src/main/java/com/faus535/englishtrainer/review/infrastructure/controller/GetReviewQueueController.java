package com.faus535.englishtrainer.review.infrastructure.controller;

import com.faus535.englishtrainer.review.application.GetReviewQueueUseCase;
import com.faus535.englishtrainer.review.domain.ReviewItem;
import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
class GetReviewQueueController {

    private final GetReviewQueueUseCase useCase;

    GetReviewQueueController(GetReviewQueueUseCase useCase) {
        this.useCase = useCase;
    }

    record ReviewItemResponse(UUID id, String sourceType, String frontContent, String backContent,
                               String nextReviewAt, int intervalDays, int consecutiveCorrect,
                               String contextSentence, String contextTranslation,
                               String targetWord, String targetTranslation) {}

    @RequireProfileOwnership
    @GetMapping("/api/profiles/{userId}/review/queue")
    ResponseEntity<List<ReviewItemResponse>> handle(@PathVariable UUID userId,
                                                     @RequestParam(defaultValue = "10") int limit) {
        List<ReviewItem> items = useCase.execute(userId, limit);
        List<ReviewItemResponse> response = items.stream()
                .map(item -> new ReviewItemResponse(
                        item.id().value(), item.sourceType().value(),
                        item.frontContent(), item.backContent(),
                        item.nextReviewAt().toString(), item.intervalDays(),
                        item.consecutiveCorrect(), item.contextSentence(),
                        item.contextTranslation(), item.targetWord(), item.targetTranslation()))
                .toList();
        return ResponseEntity.ok(response);
    }
}
