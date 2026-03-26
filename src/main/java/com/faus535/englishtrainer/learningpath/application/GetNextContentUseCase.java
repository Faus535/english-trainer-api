package com.faus535.englishtrainer.learningpath.application;

import com.faus535.englishtrainer.learningpath.domain.ContentSelector;
import com.faus535.englishtrainer.learningpath.domain.ContentSelector.ContentSelection;
import com.faus535.englishtrainer.learningpath.domain.ContentType;
import com.faus535.englishtrainer.learningpath.domain.LearningPath;
import com.faus535.englishtrainer.learningpath.domain.LearningPathRepository;
import com.faus535.englishtrainer.learningpath.domain.LearningUnit;
import com.faus535.englishtrainer.learningpath.domain.LearningUnitId;
import com.faus535.englishtrainer.learningpath.domain.LearningUnitRepository;
import com.faus535.englishtrainer.learningpath.domain.UnitContent;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionItem;
import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.vocabulary.domain.VocabEntry;
import com.faus535.englishtrainer.vocabulary.domain.VocabLevel;
import com.faus535.englishtrainer.vocabulary.domain.VocabRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@UseCase
public class GetNextContentUseCase {

    private final LearningPathRepository learningPathRepository;
    private final LearningUnitRepository learningUnitRepository;
    private final SpacedRepetitionRepository spacedRepetitionRepository;
    private final VocabRepository vocabRepository;

    public GetNextContentUseCase(LearningPathRepository learningPathRepository,
                                  LearningUnitRepository learningUnitRepository,
                                  SpacedRepetitionRepository spacedRepetitionRepository,
                                  VocabRepository vocabRepository) {
        this.learningPathRepository = learningPathRepository;
        this.learningUnitRepository = learningUnitRepository;
        this.spacedRepetitionRepository = spacedRepetitionRepository;
        this.vocabRepository = vocabRepository;
    }

    public List<UUID> execute(UserProfileId userId, ContentType type, int count) {
        Optional<LearningPath> pathOpt = learningPathRepository.findByUserId(userId);

        if (pathOpt.isEmpty()) {
            return fallbackToRandom(count);
        }

        LearningPath path = pathOpt.get();
        LearningUnitId currentUnitId = path.currentUnitId();

        if (currentUnitId == null) {
            return fallbackToRandom(count);
        }

        Optional<LearningUnit> unitOpt = learningUnitRepository.findById(currentUnitId);
        if (unitOpt.isEmpty()) {
            return fallbackToRandom(count);
        }

        LearningUnit unit = unitOpt.get();

        List<UnitContent> unpracticed = (type != null)
                ? unit.unpracticedContentsByType(type)
                : unit.unpracticedContents();

        List<SpacedRepetitionItem> dueItems = spacedRepetitionRepository
                .findDueByUser(userId, LocalDate.now());

        List<UUID> dueReviewContentIds = mapDueItemsToContentIds(dueItems);

        Instant twentyFourHoursAgo = Instant.now().minus(24, ChronoUnit.HOURS);
        Set<UUID> recentlyPracticed = ContentSelector.findRecentlyPracticed(
                unit.contents(), twentyFourHoursAgo);

        ContentSelector selector = new ContentSelector();
        ContentSelection selection = selector.select(unpracticed, dueReviewContentIds,
                recentlyPracticed, count);

        if (selection.isEmpty()) {
            return fallbackToRandom(count);
        }

        return selection.allIds();
    }

    private List<UUID> mapDueItemsToContentIds(List<SpacedRepetitionItem> dueItems) {
        return dueItems.stream()
                .filter(item -> "vocabulary-word".equals(item.itemType()))
                .map(item -> {
                    String unitRef = item.unitReference();
                    if (unitRef != null && unitRef.startsWith("vocab-")) {
                        String word = unitRef.substring("vocab-".length());
                        List<VocabEntry> entries = vocabRepository.searchByWord(word);
                        return entries.stream()
                                .filter(e -> e.en().equalsIgnoreCase(word))
                                .findFirst()
                                .map(e -> e.id().value())
                                .orElse(null);
                    }
                    return null;
                })
                .filter(id -> id != null)
                .toList();
    }

    private List<UUID> fallbackToRandom(int count) {
        return vocabRepository.findRandom(count, null).stream()
                .map(e -> e.id().value())
                .toList();
    }
}
