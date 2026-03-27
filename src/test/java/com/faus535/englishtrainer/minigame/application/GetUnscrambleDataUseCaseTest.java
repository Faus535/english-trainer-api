package com.faus535.englishtrainer.minigame.application;

import com.faus535.englishtrainer.vocabulary.domain.VocabEntryMother;
import com.faus535.englishtrainer.vocabulary.domain.VocabLevel;
import com.faus535.englishtrainer.vocabulary.infrastructure.InMemoryVocabRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

final class GetUnscrambleDataUseCaseTest {

    private InMemoryVocabRepository vocabRepository;
    private GetUnscrambleDataUseCase useCase;

    @BeforeEach
    void setUp() {
        vocabRepository = new InMemoryVocabRepository();
        useCase = new GetUnscrambleDataUseCase(vocabRepository);
    }

    @Test
    void should_return_items_with_vocabEntryId() {
        var entry = VocabEntryMother.withEnAndLevel("goodbye", "adios", "a1");
        vocabRepository.save(entry);

        List<GetUnscrambleDataUseCase.UnscrambleItem> result = useCase.execute(new VocabLevel("a1"));

        assertEquals(1, result.size());
        assertEquals("goodbye", result.getFirst().en());
        assertEquals("adios", result.getFirst().es());
        assertEquals(entry.id().value().toString(), result.getFirst().vocabEntryId());
    }

    @Test
    void should_return_empty_list_when_no_data() {
        List<GetUnscrambleDataUseCase.UnscrambleItem> result = useCase.execute(new VocabLevel("a1"));

        assertTrue(result.isEmpty());
    }
}
