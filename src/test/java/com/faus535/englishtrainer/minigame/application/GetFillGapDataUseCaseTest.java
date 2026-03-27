package com.faus535.englishtrainer.minigame.application;

import com.faus535.englishtrainer.phrase.domain.PhraseMother;
import com.faus535.englishtrainer.phrase.infrastructure.InMemoryPhraseRepository;
import com.faus535.englishtrainer.vocabulary.domain.VocabLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

final class GetFillGapDataUseCaseTest {

    private InMemoryPhraseRepository phraseRepository;
    private GetFillGapDataUseCase useCase;

    @BeforeEach
    void setUp() {
        phraseRepository = new InMemoryPhraseRepository();
        useCase = new GetFillGapDataUseCase(phraseRepository);
    }

    @Test
    void should_return_items_from_phrases() {
        var phrase = PhraseMother.withEnAndLevel("I want to go", "Quiero ir", "a1");
        phraseRepository.save(phrase);

        List<GetFillGapDataUseCase.FillGapItem> result = useCase.execute(new VocabLevel("a1"));

        assertEquals(1, result.size());
        assertEquals("I want to go", result.getFirst().en());
        assertEquals("Quiero ir", result.getFirst().es());
    }

    @Test
    void should_return_empty_list_when_no_data() {
        List<GetFillGapDataUseCase.FillGapItem> result = useCase.execute(new VocabLevel("a1"));

        assertTrue(result.isEmpty());
    }
}
