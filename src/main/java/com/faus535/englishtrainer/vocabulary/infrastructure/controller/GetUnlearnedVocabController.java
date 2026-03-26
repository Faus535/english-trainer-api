package com.faus535.englishtrainer.vocabulary.infrastructure.controller;

import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.vocabulary.application.GetUnlearnedVocabUseCase;
import com.faus535.englishtrainer.vocabulary.domain.VocabEntry;
import com.faus535.englishtrainer.vocabulary.domain.VocabLevel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
class GetUnlearnedVocabController {

    private final GetUnlearnedVocabUseCase useCase;

    GetUnlearnedVocabController(GetUnlearnedVocabUseCase useCase) {
        this.useCase = useCase;
    }

    record VocabItemResponse(UUID id, String english, String spanish, String ipa, String type,
                             String level, String category) {}

    record UnlearnedResponse(List<VocabItemResponse> items, int remainingCount) {}

    @GetMapping("/api/profiles/{userId}/vocabulary/unlearned")
    @RequireProfileOwnership
    ResponseEntity<UnlearnedResponse> handle(@PathVariable UUID userId,
                                              @RequestParam(defaultValue = "a1") String level,
                                              @RequestParam(defaultValue = "20") int count) {
        UserProfileId profileId = new UserProfileId(userId);
        VocabLevel vocabLevel = new VocabLevel(level);

        GetUnlearnedVocabUseCase.UnlearnedResult result = useCase.execute(profileId, vocabLevel, count);

        List<VocabItemResponse> items = result.items().stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(new UnlearnedResponse(items, result.remainingCount()));
    }

    private VocabItemResponse toResponse(VocabEntry entry) {
        return new VocabItemResponse(
                entry.id().value(),
                entry.en(),
                entry.es(),
                entry.ipa(),
                entry.type(),
                entry.level().value(),
                entry.category()
        );
    }
}
