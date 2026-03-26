package com.faus535.englishtrainer.vocabulary.infrastructure.event;

import com.faus535.englishtrainer.spacedrepetition.application.AddVocabularyToReviewUseCase;
import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionItem;
import com.faus535.englishtrainer.spacedrepetition.infrastructure.InMemorySpacedRepetitionRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.vocabulary.domain.VocabEntryId;
import com.faus535.englishtrainer.vocabulary.domain.VocabMasteryId;
import com.faus535.englishtrainer.vocabulary.domain.event.WordLearnedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class WordLearnedListenerTest {

    private InMemorySpacedRepetitionRepository spacedRepetitionRepository;
    private WordLearnedListener listener;

    @BeforeEach
    void setUp() {
        spacedRepetitionRepository = new InMemorySpacedRepetitionRepository();
        AddVocabularyToReviewUseCase addVocabularyToReviewUseCase =
                new AddVocabularyToReviewUseCase(spacedRepetitionRepository);
        listener = new WordLearnedListener(addVocabularyToReviewUseCase);
    }

    @Test
    void should_schedule_srs_review_when_word_is_learned() {
        UserProfileId userId = UserProfileId.generate();
        WordLearnedEvent event = new WordLearnedEvent(
                VocabMasteryId.generate(), userId, VocabEntryId.generate(), "hello");

        listener.handleWordLearned(event);

        var items = spacedRepetitionRepository.findAllByUser(userId);
        assertEquals(1, items.size());
        SpacedRepetitionItem item = items.get(0);
        assertEquals("vocab-hello", item.unitReference());
        assertEquals("vocabulary-word", item.itemType());
        assertFalse(item.graduated());
    }

    @Test
    void should_not_duplicate_srs_item_when_already_exists() {
        UserProfileId userId = UserProfileId.generate();
        WordLearnedEvent event = new WordLearnedEvent(
                VocabMasteryId.generate(), userId, VocabEntryId.generate(), "hello");

        listener.handleWordLearned(event);
        listener.handleWordLearned(event);

        var items = spacedRepetitionRepository.findAllByUser(userId);
        assertEquals(1, items.size());
    }
}
