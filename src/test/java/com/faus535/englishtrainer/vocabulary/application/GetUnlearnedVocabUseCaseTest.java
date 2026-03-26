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

final class GetUnlearnedVocabUseCaseTest {

    private InMemoryVocabRepository vocabRepository;
    private InMemoryVocabMasteryRepository masteryRepository;
    private GetUnlearnedVocabUseCase useCase;
    private final UserProfileId userId = new UserProfileId(UUID.randomUUID());
    private final VocabLevel a1 = new VocabLevel("a1");

    @BeforeEach
    void setUp() {
        vocabRepository = new InMemoryVocabRepository();
        masteryRepository = new InMemoryVocabMasteryRepository();
        useCase = new GetUnlearnedVocabUseCase(vocabRepository, masteryRepository);
    }

    @Test
    void shouldReturnAllEntriesWhenNoneAreLearned() {
        vocabRepository.save(createEntry("hello", a1));
        vocabRepository.save(createEntry("world", a1));

        GetUnlearnedVocabUseCase.UnlearnedResult result = useCase.execute(userId, a1, 20);

        assertEquals(2, result.items().size());
        assertEquals(2, result.remainingCount());
    }

    @Test
    void shouldExcludeLearnedWords() {
        VocabEntry hello = createEntry("hello", a1);
        VocabEntry world = createEntry("world", a1);
        vocabRepository.save(hello);
        vocabRepository.save(world);

        VocabMastery mastery = VocabMastery.create(userId, hello.id(), "hello", new MasterySource("game", "test"));
        // Simulate LEARNED status by recording enough correct answers
        mastery = mastery.recordCorrectAnswer().recordCorrectAnswer().recordCorrectAnswer();
        assertEquals(VocabMasteryStatus.LEARNED, mastery.status());
        masteryRepository.save(mastery);

        GetUnlearnedVocabUseCase.UnlearnedResult result = useCase.execute(userId, a1, 20);

        assertEquals(1, result.items().size());
        assertEquals("world", result.items().get(0).en());
        assertEquals(1, result.remainingCount());
    }

    @Test
    void shouldExcludeMasteredWords() {
        VocabEntry hello = createEntry("hello", a1);
        VocabEntry world = createEntry("world", a1);
        vocabRepository.save(hello);
        vocabRepository.save(world);

        VocabMastery mastery = VocabMastery.create(userId, hello.id(), "hello", new MasterySource("game", "test"));
        mastery = mastery.graduate();
        assertEquals(VocabMasteryStatus.MASTERED, mastery.status());
        masteryRepository.save(mastery);

        GetUnlearnedVocabUseCase.UnlearnedResult result = useCase.execute(userId, a1, 20);

        assertEquals(1, result.items().size());
        assertEquals(1, result.remainingCount());
    }

    @Test
    void shouldReturnEmptyWhenAllMastered() {
        VocabEntry hello = createEntry("hello", a1);
        VocabEntry world = createEntry("world", a1);
        vocabRepository.save(hello);
        vocabRepository.save(world);

        VocabMastery mastery1 = VocabMastery.create(userId, hello.id(), "hello", new MasterySource("game", "test"));
        mastery1 = mastery1.graduate();
        masteryRepository.save(mastery1);

        VocabMastery mastery2 = VocabMastery.create(userId, world.id(), "world", new MasterySource("game", "test"));
        mastery2 = mastery2.graduate();
        masteryRepository.save(mastery2);

        GetUnlearnedVocabUseCase.UnlearnedResult result = useCase.execute(userId, a1, 20);

        assertTrue(result.items().isEmpty());
        assertEquals(0, result.remainingCount());
    }

    @Test
    void shouldNotExcludeLearningWords() {
        VocabEntry hello = createEntry("hello", a1);
        vocabRepository.save(hello);

        VocabMastery mastery = VocabMastery.create(userId, hello.id(), "hello", new MasterySource("game", "test"));
        assertEquals(VocabMasteryStatus.LEARNING, mastery.status());
        masteryRepository.save(mastery);

        GetUnlearnedVocabUseCase.UnlearnedResult result = useCase.execute(userId, a1, 20);

        assertEquals(1, result.items().size());
    }

    @Test
    void shouldRespectCountLimit() {
        for (int i = 0; i < 10; i++) {
            vocabRepository.save(createEntry("word" + i, a1));
        }

        GetUnlearnedVocabUseCase.UnlearnedResult result = useCase.execute(userId, a1, 3);

        assertEquals(3, result.items().size());
        assertEquals(10, result.remainingCount());
    }

    private VocabEntry createEntry(String word, VocabLevel level) {
        return VocabEntry.create(
                VocabEntryId.generate(),
                word,
                "/test/",
                word + "_es",
                "noun",
                "Example with " + word,
                level
        );
    }
}
