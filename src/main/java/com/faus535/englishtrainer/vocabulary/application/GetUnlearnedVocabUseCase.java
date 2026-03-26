package com.faus535.englishtrainer.vocabulary.application;

import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.vocabulary.domain.VocabEntry;
import com.faus535.englishtrainer.vocabulary.domain.VocabLevel;
import com.faus535.englishtrainer.vocabulary.domain.VocabMastery;
import com.faus535.englishtrainer.vocabulary.domain.VocabMasteryRepository;
import com.faus535.englishtrainer.vocabulary.domain.VocabMasteryStatus;
import com.faus535.englishtrainer.vocabulary.domain.VocabRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UseCase
public class GetUnlearnedVocabUseCase {

    private final VocabRepository vocabRepository;
    private final VocabMasteryRepository vocabMasteryRepository;

    public GetUnlearnedVocabUseCase(VocabRepository vocabRepository, VocabMasteryRepository vocabMasteryRepository) {
        this.vocabRepository = vocabRepository;
        this.vocabMasteryRepository = vocabMasteryRepository;
    }

    public record UnlearnedResult(List<VocabEntry> items, int remainingCount) {}

    public UnlearnedResult execute(UserProfileId userId, VocabLevel level, int count) {
        Set<UUID> masteredIds = getMasteredVocabEntryIds(userId);

        List<VocabEntry> unlearned = vocabRepository.findByLevelExcludingIds(level, masteredIds, count);

        int totalAtLevel = vocabRepository.findByLevel(level).size();
        int learnedOrMasteredAtLevel = (int) vocabRepository.findByLevel(level).stream()
                .filter(entry -> masteredIds.contains(entry.id().value()))
                .count();
        int remainingCount = totalAtLevel - learnedOrMasteredAtLevel;

        return new UnlearnedResult(unlearned, remainingCount);
    }

    private Set<UUID> getMasteredVocabEntryIds(UserProfileId userId) {
        List<VocabMastery> learned = vocabMasteryRepository.findByUserIdAndStatus(userId, VocabMasteryStatus.LEARNED);
        List<VocabMastery> mastered = vocabMasteryRepository.findByUserIdAndStatus(userId, VocabMasteryStatus.MASTERED);

        return Stream.concat(learned.stream(), mastered.stream())
                .map(m -> m.vocabEntryId().value())
                .collect(Collectors.toSet());
    }
}
