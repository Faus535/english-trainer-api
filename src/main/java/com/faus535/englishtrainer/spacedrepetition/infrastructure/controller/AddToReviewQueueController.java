package com.faus535.englishtrainer.spacedrepetition.infrastructure.controller;

import com.faus535.englishtrainer.spacedrepetition.application.AddToReviewQueueUseCase;
import com.faus535.englishtrainer.spacedrepetition.application.AddVocabularyToReviewUseCase;
import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionItem;
import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class AddToReviewQueueController {

    private final AddToReviewQueueUseCase addToReviewUseCase;
    private final AddVocabularyToReviewUseCase addVocabularyUseCase;

    AddToReviewQueueController(AddToReviewQueueUseCase addToReviewUseCase,
                                AddVocabularyToReviewUseCase addVocabularyUseCase) {
        this.addToReviewUseCase = addToReviewUseCase;
        this.addVocabularyUseCase = addVocabularyUseCase;
    }

    record AddReviewRequest(@NotBlank String itemType, String moduleName, String level,
                            Integer unitIndex, String word) {}

    @PostMapping("/api/profiles/{userId}/reviews")
    @RequireProfileOwnership
    ResponseEntity<SpacedRepetitionItemResponse> handle(@PathVariable String userId,
                                                        @Valid @RequestBody AddReviewRequest request,
                                                        Authentication authentication) {
        UserProfileId profileId = UserProfileId.fromString(userId);
        SpacedRepetitionItem item = switch (request.itemType()) {
            case "vocabulary-word" -> addVocabularyUseCase.execute(profileId, request.word(), request.level());
            default -> addToReviewUseCase.execute(profileId, request.moduleName(), request.level(),
                    request.unitIndex() != null ? request.unitIndex() : 0);
        };
        return ResponseEntity.status(HttpStatus.CREATED).body(SpacedRepetitionItemResponse.from(item));
    }
}
