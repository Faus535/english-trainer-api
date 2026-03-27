package com.faus535.englishtrainer.minigame.application;

import com.faus535.englishtrainer.vocabulary.domain.VocabEntryMother;
import com.faus535.englishtrainer.vocabulary.domain.VocabLevel;
import com.faus535.englishtrainer.vocabulary.infrastructure.InMemoryVocabRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

final class GetWordMatchDataUseCaseTest {

    private InMemoryVocabRepository vocabRepository;
    private GetWordMatchDataUseCase useCase;

    @BeforeEach
    void setUp() {
        vocabRepository = new InMemoryVocabRepository();
        useCase = new GetWordMatchDataUseCase(vocabRepository);
    }

    @Test
    void should_return_items_with_vocabEntryId() {
        var entry = VocabEntryMother.withEnAndLevel("hello", "hola", "a1");
        vocabRepository.save(entry);

        List<GetWordMatchDataUseCase.WordMatchPair> result = useCase.execute(new VocabLevel("a1"));

        assertEquals(1, result.size());
        assertEquals("hello", result.getFirst().en());
        assertEquals("hola", result.getFirst().es());
        assertEquals(entry.id().value().toString(), result.getFirst().vocabEntryId());
    }

    @Test
    void should_return_empty_list_when_no_data() {
        List<GetWordMatchDataUseCase.WordMatchPair> result = useCase.execute(new VocabLevel("a1"));

        assertTrue(result.isEmpty());
    }
}
