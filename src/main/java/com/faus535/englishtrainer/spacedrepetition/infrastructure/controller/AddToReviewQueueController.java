package com.faus535.englishtrainer.spacedrepetition.infrastructure.controller;

import com.faus535.englishtrainer.spacedrepetition.application.AddToReviewQueueUseCase;
import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionItem;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class AddToReviewQueueController {

    private final AddToReviewQueueUseCase useCase;

    AddToReviewQueueController(AddToReviewQueueUseCase useCase) {
        this.useCase = useCase;
    }

    record AddReviewRequest(@NotBlank String moduleName, @NotBlank String level, @NotNull @Min(0) Integer unitIndex) {}

    @PostMapping("/api/profiles/{userId}/reviews")
    ResponseEntity<SpacedRepetitionItemResponse> handle(@PathVariable String userId,
                                                        @Valid @RequestBody AddReviewRequest request) {
        SpacedRepetitionItem item = useCase.execute(
                UserProfileId.fromString(userId),
                request.moduleName(),
                request.level(),
                request.unitIndex()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(SpacedRepetitionItemResponse.from(item));
    }
}
