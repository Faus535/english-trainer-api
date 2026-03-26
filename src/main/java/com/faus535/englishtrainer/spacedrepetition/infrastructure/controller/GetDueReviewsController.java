package com.faus535.englishtrainer.spacedrepetition.infrastructure.controller;

import com.faus535.englishtrainer.spacedrepetition.application.GetDueReviewsUseCase;
import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionItem;
import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class GetDueReviewsController {

    private final GetDueReviewsUseCase useCase;

    GetDueReviewsController(GetDueReviewsUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/api/profiles/{userId}/reviews/due")
    @RequireProfileOwnership
    ResponseEntity<List<SpacedRepetitionItemResponse>> handle(@PathVariable String userId,
                                                              Authentication authentication) {
        List<SpacedRepetitionItem> items = useCase.execute(UserProfileId.fromString(userId));
        List<SpacedRepetitionItemResponse> response = items.stream()
                .map(SpacedRepetitionItemResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }
}
