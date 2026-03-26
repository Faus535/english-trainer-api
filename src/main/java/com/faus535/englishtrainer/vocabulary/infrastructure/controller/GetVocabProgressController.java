package com.faus535.englishtrainer.vocabulary.infrastructure.controller;

import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.vocabulary.application.GetVocabProgressUseCase;
import com.faus535.englishtrainer.vocabulary.domain.VocabLevel;
import com.faus535.englishtrainer.vocabulary.domain.VocabMastery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
class GetVocabProgressController {

    private final GetVocabProgressUseCase useCase;

    GetVocabProgressController(GetVocabProgressUseCase useCase) {
        this.useCase = useCase;
    }

    record StatsResponse(int totalEncountered, int totalLearned, int totalMastered, double averageAccuracy) {}

    record WordProgressResponse(String word, UUID vocabEntryId, int correctCount, int totalAttempts,
                                 double accuracy, String lastPracticedAt, String learnedAt) {}

    record VocabProgressResponse(StatsResponse stats, List<WordProgressResponse> learning,
                                  List<WordProgressResponse> learned, List<WordProgressResponse> mastered) {}

    @GetMapping("/api/profiles/{userId}/vocabulary/progress")
    @RequireProfileOwnership
    ResponseEntity<VocabProgressResponse> handle(@PathVariable UUID userId,
                                                  @RequestParam(required = false) String level,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "50") int size) {
        UserProfileId profileId = new UserProfileId(userId);
        VocabLevel vocabLevel = level != null ? new VocabLevel(level) : null;

        GetVocabProgressUseCase.VocabProgressResult result = useCase.execute(profileId, vocabLevel, page, size);

        StatsResponse stats = new StatsResponse(
                result.stats().totalEncountered(),
                result.stats().totalLearned(),
                result.stats().totalMastered(),
                result.stats().averageAccuracy()
        );

        List<WordProgressResponse> learning = toWordProgressList(result.learning());
        List<WordProgressResponse> learned = toWordProgressList(result.learned());
        List<WordProgressResponse> mastered = toWordProgressList(result.mastered());

        return ResponseEntity.ok(new VocabProgressResponse(stats, learning, learned, mastered));
    }

    private List<WordProgressResponse> toWordProgressList(List<GetVocabProgressUseCase.WordProgress> items) {
        return items.stream()
                .map(wp -> {
                    VocabMastery m = wp.mastery();
                    return new WordProgressResponse(
                            m.word(),
                            m.vocabEntryId().value(),
                            m.correctCount(),
                            m.totalAttempts(),
                            m.accuracy(),
                            formatInstant(m.lastPracticedAt()),
                            formatInstant(m.learnedAt())
                    );
                })
                .toList();
    }

    private String formatInstant(Instant instant) {
        return instant != null ? instant.toString() : null;
    }
}
