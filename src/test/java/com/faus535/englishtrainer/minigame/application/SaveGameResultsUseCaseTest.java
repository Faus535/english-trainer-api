package com.faus535.englishtrainer.minigame.application;

import com.faus535.englishtrainer.minigame.infrastructure.InMemoryMiniGameScoreRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.vocabulary.domain.MasterySource;
import com.faus535.englishtrainer.vocabulary.domain.VocabEntryId;
import com.faus535.englishtrainer.vocabulary.domain.VocabMastery;
import com.faus535.englishtrainer.vocabulary.domain.VocabMasteryStatus;
import com.faus535.englishtrainer.vocabulary.infrastructure.InMemoryVocabMasteryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

final class SaveGameResultsUseCaseTest {

    private InMemoryMiniGameScoreRepository scoreRepository;
    private InMemoryVocabMasteryRepository masteryRepository;
    private ApplicationEventPublisher eventPublisher;
    private SaveGameResultsUseCase useCase;
    private final List<Object> publishedEvents = new ArrayList<>();

    private static final UserProfileId USER_ID = new UserProfileId(UUID.randomUUID());

    @BeforeEach
    void setUp() {
        scoreRepository = new InMemoryMiniGameScoreRepository();
        masteryRepository = new InMemoryVocabMasteryRepository();
        eventPublisher = publishedEvents::add;

        SaveMiniGameScoreUseCase saveScoreUseCase = new SaveMiniGameScoreUseCase(scoreRepository);
        useCase = new SaveGameResultsUseCase(saveScoreUseCase, masteryRepository, eventPublisher);
        publishedEvents.clear();
    }

    @Test
    void shouldCreateMasteryForNewWords() {
        UUID vocabEntryId = UUID.randomUUID();
        List<SaveGameResultsUseCase.AnsweredItem> items = List.of(
                new SaveGameResultsUseCase.AnsweredItem(vocabEntryId, "hello", "A1", true)
        );

        SaveGameResultsUseCase.SaveGameResult result = useCase.execute(USER_ID, "word_match", 80, items);

        assertEquals(1, result.wordsAddedToReview());
        assertEquals(1, result.totalWordsEncountered());
        assertEquals(1, masteryRepository.count());
    }

    @Test
    void shouldUpdateMasteryForExistingWords() {
        VocabEntryId vocabEntryId = VocabEntryId.generate();
        VocabMastery existing = VocabMastery.create(USER_ID, vocabEntryId, "hello",
                new MasterySource("minigame", "word_match"));
        masteryRepository.save(existing);

        List<SaveGameResultsUseCase.AnsweredItem> items = List.of(
                new SaveGameResultsUseCase.AnsweredItem(vocabEntryId.value(), "hello", "A1", true)
        );

        SaveGameResultsUseCase.SaveGameResult result = useCase.execute(USER_ID, "word_match", 80, items);

        assertEquals(0, result.wordsAddedToReview());
        assertEquals(1, masteryRepository.count());

        VocabMastery updated = masteryRepository.findByUserIdAndVocabEntryId(USER_ID, vocabEntryId).orElseThrow();
        assertEquals(1, updated.correctCount());
        assertEquals(1, updated.totalAttempts());
    }

    @Test
    void shouldSkipItemsWithNullVocabEntryId() {
        List<SaveGameResultsUseCase.AnsweredItem> items = List.of(
                new SaveGameResultsUseCase.AnsweredItem(null, "unknown", "A1", true)
        );

        SaveGameResultsUseCase.SaveGameResult result = useCase.execute(USER_ID, "word_match", 80, items);

        assertEquals(0, result.wordsAddedToReview());
        assertEquals(1, result.totalWordsEncountered());
        assertEquals(0, masteryRepository.count());
    }

    @Test
    void shouldBeBackwardCompatibleWithEmptyAnsweredItems() {
        SaveGameResultsUseCase.SaveGameResult result = useCase.execute(USER_ID, "word_match", 80, List.of());

        assertEquals(80, result.score());
        assertTrue(result.xpEarned() > 0);
        assertTrue(result.wordsLearned().isEmpty());
        assertEquals(0, result.wordsAddedToReview());
        assertEquals(0, result.totalWordsEncountered());
    }

    @Test
    void shouldReportWordsReachingLearnedStatus() {
        VocabEntryId vocabEntryId = VocabEntryId.generate();
        // Pre-seed mastery with 2 correct answers and 70%+ accuracy so the next correct tips it to LEARNED
        VocabMastery existing = VocabMastery.create(USER_ID, vocabEntryId, "excellent",
                new MasterySource("minigame", "word_match"));
        existing = existing.recordCorrectAnswer(); // 1 correct, 1 total
        existing = existing.recordCorrectAnswer(); // 2 correct, 2 total
        // Pull events from setup so they don't interfere
        existing.pullDomainEvents();
        masteryRepository.save(existing);

        List<SaveGameResultsUseCase.AnsweredItem> items = List.of(
                new SaveGameResultsUseCase.AnsweredItem(vocabEntryId.value(), "excellent", "B1", true)
        );

        SaveGameResultsUseCase.SaveGameResult result = useCase.execute(USER_ID, "word_match", 90, items);

        assertTrue(result.wordsLearned().contains("excellent"));
        assertFalse(publishedEvents.isEmpty());

        VocabMastery updated = masteryRepository.findByUserIdAndVocabEntryId(USER_ID, vocabEntryId).orElseThrow();
        assertEquals(VocabMasteryStatus.LEARNED, updated.status());
    }
}
