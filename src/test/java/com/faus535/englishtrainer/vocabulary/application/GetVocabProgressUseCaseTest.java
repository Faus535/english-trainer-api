package com.faus535.englishtrainer.vocabulary.application;

import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.vocabulary.domain.MasterySource;
import com.faus535.englishtrainer.vocabulary.domain.VocabEntry;
import com.faus535.englishtrainer.vocabulary.domain.VocabEntryId;
import com.faus535.englishtrainer.vocabulary.domain.VocabLevel;
import com.faus535.englishtrainer.vocabulary.domain.VocabMastery;
import com.faus535.englishtrainer.vocabulary.domain.VocabMasteryStatus;
import com.faus535.englishtrainer.vocabulary.infrastructure.InMemoryVocabMasteryRepository;
import com.faus535.englishtrainer.vocabulary.infrastructure.InMemoryVocabRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

final class GetVocabProgressUseCaseTest {

    private InMemoryVocabRepository vocabRepository;
    private InMemoryVocabMasteryRepository masteryRepository;
    private GetVocabProgressUseCase useCase;
    private final UserProfileId userId = new UserProfileId(UUID.randomUUID());
    private final VocabLevel a1 = new VocabLevel("a1");
    private final VocabLevel b1 = new VocabLevel("b1");

    @BeforeEach
    void setUp() {
        vocabRepository = new InMemoryVocabRepository();
        masteryRepository = new InMemoryVocabMasteryRepository();
        useCase = new GetVocabProgressUseCase(masteryRepository, vocabRepository);
    }

    @Test
    void shouldGroupByStatus() {
        VocabEntry hello = createAndSaveEntry("hello", a1);
        VocabEntry world = createAndSaveEntry("world", a1);
        VocabEntry bye = createAndSaveEntry("bye", a1);

        // LEARNING
        VocabMastery learningMastery = VocabMastery.create(userId, hello.id(), "hello", new MasterySource("game", "test"));
        masteryRepository.save(learningMastery);

        // LEARNED (3 correct answers with 100% accuracy)
        VocabMastery learnedMastery = VocabMastery.create(userId, world.id(), "world", new MasterySource("game", "test"));
        learnedMastery = learnedMastery.recordCorrectAnswer().recordCorrectAnswer().recordCorrectAnswer();
        masteryRepository.save(learnedMastery);

        // MASTERED
        VocabMastery masteredMastery = VocabMastery.create(userId, bye.id(), "bye", new MasterySource("game", "test"));
        masteredMastery = masteredMastery.graduate();
        masteryRepository.save(masteredMastery);

        GetVocabProgressUseCase.VocabProgressResult result = useCase.execute(userId, a1, 0, 50);

        assertEquals(1, result.learning().size());
        assertEquals(1, result.learned().size());
        assertEquals(1, result.mastered().size());
    }

    @Test
    void shouldCalculateStats() {
        VocabEntry hello = createAndSaveEntry("hello", a1);
        VocabEntry world = createAndSaveEntry("world", a1);

        VocabMastery mastery1 = VocabMastery.create(userId, hello.id(), "hello", new MasterySource("game", "test"));
        mastery1 = mastery1.recordCorrectAnswer().recordCorrectAnswer().recordCorrectAnswer();
        masteryRepository.save(mastery1);

        VocabMastery mastery2 = VocabMastery.create(userId, world.id(), "world", new MasterySource("game", "test"));
        mastery2 = mastery2.graduate();
        masteryRepository.save(mastery2);

        GetVocabProgressUseCase.VocabProgressResult result = useCase.execute(userId, a1, 0, 50);

        assertEquals(2, result.stats().totalEncountered());
        assertEquals(1, result.stats().totalLearned());
        assertEquals(1, result.stats().totalMastered());
    }

    @Test
    void shouldFilterByLevel() {
        VocabEntry a1Entry = createAndSaveEntry("hello", a1);
        VocabEntry b1Entry = createAndSaveEntry("however", b1);

        VocabMastery mastery1 = VocabMastery.create(userId, a1Entry.id(), "hello", new MasterySource("game", "test"));
        masteryRepository.save(mastery1);

        VocabMastery mastery2 = VocabMastery.create(userId, b1Entry.id(), "however", new MasterySource("game", "test"));
        masteryRepository.save(mastery2);

        GetVocabProgressUseCase.VocabProgressResult result = useCase.execute(userId, a1, 0, 50);

        assertEquals(1, result.stats().totalEncountered());
        assertEquals(1, result.learning().size());
        assertEquals("hello", result.learning().get(0).mastery().word());
    }

    @Test
    void shouldReturnAllLevelsWhenLevelIsNull() {
        VocabEntry a1Entry = createAndSaveEntry("hello", a1);
        VocabEntry b1Entry = createAndSaveEntry("however", b1);

        VocabMastery mastery1 = VocabMastery.create(userId, a1Entry.id(), "hello", new MasterySource("game", "test"));
        masteryRepository.save(mastery1);

        VocabMastery mastery2 = VocabMastery.create(userId, b1Entry.id(), "however", new MasterySource("game", "test"));
        masteryRepository.save(mastery2);

        GetVocabProgressUseCase.VocabProgressResult result = useCase.execute(userId, null, 0, 50);

        assertEquals(2, result.stats().totalEncountered());
    }

    @Test
    void shouldReturnEmptyWhenNoProgress() {
        GetVocabProgressUseCase.VocabProgressResult result = useCase.execute(userId, a1, 0, 50);

        assertEquals(0, result.stats().totalEncountered());
        assertEquals(0, result.stats().totalLearned());
        assertEquals(0, result.stats().totalMastered());
        assertEquals(0.0, result.stats().averageAccuracy());
        assertTrue(result.learning().isEmpty());
        assertTrue(result.learned().isEmpty());
        assertTrue(result.mastered().isEmpty());
    }

    @Test
    void shouldPaginateResults() {
        for (int i = 0; i < 5; i++) {
            VocabEntry entry = createAndSaveEntry("word" + i, a1);
            VocabMastery mastery = VocabMastery.create(userId, entry.id(), "word" + i, new MasterySource("game", "test"));
            masteryRepository.save(mastery);
        }

        GetVocabProgressUseCase.VocabProgressResult page0 = useCase.execute(userId, a1, 0, 3);
        GetVocabProgressUseCase.VocabProgressResult page1 = useCase.execute(userId, a1, 1, 3);

        assertEquals(3, page0.learning().size());
        assertEquals(2, page1.learning().size());
        assertEquals(5, page0.stats().totalEncountered());
    }

    private VocabEntry createAndSaveEntry(String word, VocabLevel level) {
        VocabEntry entry = VocabEntry.create(
                VocabEntryId.generate(),
                word,
                "/test/",
                word + "_es",
                "noun",
                "Example with " + word,
                level
        );
        vocabRepository.save(entry);
        return entry;
    }
}
