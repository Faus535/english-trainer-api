package com.faus535.englishtrainer.spacedrepetition.infrastructure.controller;

import com.faus535.englishtrainer.spacedrepetition.application.CompleteReviewUseCase;
import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionItem;
import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionItemId;
import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import com.faus535.englishtrainer.spacedrepetition.domain.error.SpacedRepetitionItemNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class CompleteReviewController {

    private final CompleteReviewUseCase useCase;

    CompleteReviewController(CompleteReviewUseCase useCase) {
        this.useCase = useCase;
    }

    @PutMapping("/api/profiles/{userId}/reviews/{itemId}/complete")
    @RequireProfileOwnership
    ResponseEntity<SpacedRepetitionItemResponse> handle(@PathVariable String userId,
                                                        @PathVariable String itemId,
                                                        Authentication authentication)
            throws SpacedRepetitionItemNotFoundException {
        SpacedRepetitionItem item = useCase.execute(SpacedRepetitionItemId.fromString(itemId));
        return ResponseEntity.ok(SpacedRepetitionItemResponse.from(item));
    }
}
