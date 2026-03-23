package com.faus535.englishtrainer.minimalpair.infrastructure.controller;

import com.faus535.englishtrainer.minimalpair.application.RecordMinimalPairResultUseCase;
import com.faus535.englishtrainer.minimalpair.domain.MinimalPairId;
import com.faus535.englishtrainer.minimalpair.domain.MinimalPairResult;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
class RecordMinimalPairResultController {

    private final RecordMinimalPairResultUseCase useCase;

    RecordMinimalPairResultController(RecordMinimalPairResultUseCase useCase) {
        this.useCase = useCase;
    }

    record RecordRequest(@NotNull UUID pairId, @NotNull Boolean correct) {}

    record RecordResponse(String id, String userId, String pairId, boolean correct, String answeredAt) {}

    @PostMapping("/api/profiles/{userId}/pronunciation/minimal-pairs/results")
    ResponseEntity<RecordResponse> handle(@PathVariable UUID userId,
                                           @Valid @RequestBody RecordRequest request) {

        MinimalPairResult result = useCase.execute(
                new UserProfileId(userId),
                new MinimalPairId(request.pairId()),
                request.correct()
        );

        return ResponseEntity.ok(new RecordResponse(
                result.id().value().toString(),
                result.userId().value().toString(),
                result.pairId().value().toString(),
                result.correct(),
                result.answeredAt().toString()
        ));
    }
}
