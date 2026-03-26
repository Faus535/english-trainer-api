package com.faus535.englishtrainer.vocabulary.application;

import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.vocabulary.domain.VocabEntry;
import com.faus535.englishtrainer.vocabulary.domain.VocabLevel;
import com.faus535.englishtrainer.vocabulary.domain.VocabMastery;
import com.faus535.englishtrainer.vocabulary.domain.VocabMasteryRepository;
import com.faus535.englishtrainer.vocabulary.domain.VocabMasteryStatus;
import com.faus535.englishtrainer.vocabulary.domain.VocabRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@UseCase
public class GetVocabProgressUseCase {

    private final VocabMasteryRepository vocabMasteryRepository;
    private final VocabRepository vocabRepository;

    public GetVocabProgressUseCase(VocabMasteryRepository vocabMasteryRepository, VocabRepository vocabRepository) {
        this.vocabMasteryRepository = vocabMasteryRepository;
        this.vocabRepository = vocabRepository;
    }

    public record Stats(int totalEncountered, int totalLearned, int totalMastered, double averageAccuracy) {}

    public record WordProgress(VocabMastery mastery) {}

    public record VocabProgressResult(Stats stats, List<WordProgress> learning, List<WordProgress> learned,
                                       List<WordProgress> mastered) {}

    public VocabProgressResult execute(UserProfileId userId, VocabLevel level, int page, int size) {
        List<VocabMastery> allMasteries = vocabMasteryRepository.findByUserId(userId);

        List<VocabMastery> filtered;
        if (level != null) {
            Set<UUID> vocabIdsAtLevel = vocabRepository.findByLevel(level).stream()
                    .map(entry -> entry.id().value())
                    .collect(Collectors.toSet());
            filtered = allMasteries.stream()
                    .filter(m -> vocabIdsAtLevel.contains(m.vocabEntryId().value()))
                    .toList();
        } else {
            filtered = allMasteries;
        }

        Map<VocabMasteryStatus, List<VocabMastery>> grouped = filtered.stream()
                .collect(Collectors.groupingBy(VocabMastery::status));

        List<VocabMastery> learningList = grouped.getOrDefault(VocabMasteryStatus.LEARNING, List.of());
        List<VocabMastery> learnedList = grouped.getOrDefault(VocabMasteryStatus.LEARNED, List.of());
        List<VocabMastery> masteredList = grouped.getOrDefault(VocabMasteryStatus.MASTERED, List.of());

        double avgAccuracy = filtered.isEmpty() ? 0.0 :
                filtered.stream().mapToDouble(VocabMastery::accuracy).average().orElse(0.0);

        Stats stats = new Stats(filtered.size(), learnedList.size(), masteredList.size(),
                Math.round(avgAccuracy * 100.0) / 100.0);

        List<WordProgress> learningProgress = paginate(learningList, page, size);
        List<WordProgress> learnedProgress = paginate(learnedList, page, size);
        List<WordProgress> masteredProgress = paginate(masteredList, page, size);

        return new VocabProgressResult(stats, learningProgress, learnedProgress, masteredProgress);
    }

    private List<WordProgress> paginate(List<VocabMastery> items, int page, int size) {
        int start = page * size;
        if (start >= items.size()) {
            return List.of();
        }
        int end = Math.min(start + size, items.size());
        return items.subList(start, end).stream()
                .map(WordProgress::new)
                .toList();
    }
}
