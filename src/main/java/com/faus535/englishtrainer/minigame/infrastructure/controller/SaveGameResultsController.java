package com.faus535.englishtrainer.minigame.infrastructure.controller;

import com.faus535.englishtrainer.minigame.application.SaveGameResultsUseCase;
import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
class SaveGameResultsController {

    private final SaveGameResultsUseCase useCase;

    SaveGameResultsController(SaveGameResultsUseCase useCase) {
        this.useCase = useCase;
    }

    record AnsweredItemDto(UUID vocabEntryId, String word, @NotBlank String level, boolean correct) {}

    record SaveGameResultsRequest(@NotBlank String gameType,
                                   @NotNull @Min(0) @Max(10000) Integer score,
                                   @NotNull @Size(max = 200) @Valid List<AnsweredItemDto> answeredItems) {}

    record SaveGameResultsResponse(int score, int xpEarned, List<String> wordsLearned,
                                    int wordsAddedToReview, int totalWordsEncountered) {}

    @PostMapping("/api/profiles/{userId}/minigames/results")
    @RequireProfileOwnership
    ResponseEntity<SaveGameResultsResponse> handle(@PathVariable UUID userId,
                                                    @Valid @RequestBody SaveGameResultsRequest request) {
        UserProfileId profileId = new UserProfileId(userId);

        List<SaveGameResultsUseCase.AnsweredItem> answeredItems = request.answeredItems().stream()
                .map(dto -> new SaveGameResultsUseCase.AnsweredItem(
                        dto.vocabEntryId(), dto.word(), dto.level(), dto.correct()))
                .toList();

        SaveGameResultsUseCase.SaveGameResult result =
                useCase.execute(profileId, request.gameType(), request.score(), answeredItems);

        return ResponseEntity.ok(new SaveGameResultsResponse(
                result.score(),
                result.xpEarned(),
                result.wordsLearned(),
                result.wordsAddedToReview(),
                result.totalWordsEncountered()
        ));
    }
}
