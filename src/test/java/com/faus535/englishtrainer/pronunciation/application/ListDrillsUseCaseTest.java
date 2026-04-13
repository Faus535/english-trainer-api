package com.faus535.englishtrainer.pronunciation.application;

import com.faus535.englishtrainer.pronunciation.domain.PronunciationDrill;
import com.faus535.englishtrainer.pronunciation.domain.PronunciationDrillMother;
import com.faus535.englishtrainer.pronunciation.infrastructure.InMemoryPronunciationDrillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ListDrillsUseCaseTest {

    private InMemoryPronunciationDrillRepository drillRepository;
    private ListDrillsUseCase useCase;

    @BeforeEach
    void setUp() {
        drillRepository = new InMemoryPronunciationDrillRepository();
        useCase = new ListDrillsUseCase(drillRepository);
    }

    @Test
    void shouldReturnDrillsFilteredByLevel() {
        drillRepository.add(PronunciationDrillMother.withFocusAndLevel("th-sound", "b1"));
        drillRepository.add(PronunciationDrillMother.withFocusAndLevel("sh-vs-s", "b1"));
        drillRepository.add(PronunciationDrillMother.withFocusAndLevel("vowels", "a2"));

        List<PronunciationDrillDto> result = useCase.execute("b1", null);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(d -> "b1".equals(d.cefrLevel())));
    }

    @Test
    void shouldReturnDrillsFilteredByLevelAndFocus() {
        drillRepository.add(PronunciationDrillMother.withFocusAndLevel("th-sound", "b1"));
        drillRepository.add(PronunciationDrillMother.withFocusAndLevel("sh-vs-s", "b1"));

        List<PronunciationDrillDto> result = useCase.execute("b1", "th-sound");

        assertEquals(1, result.size());
        assertEquals("th-sound", result.getFirst().focus());
    }

    @Test
    void shouldReturnEmptyListWhenNoDrillsMatch() {
        drillRepository.add(PronunciationDrillMother.withFocusAndLevel("th-sound", "b2"));

        List<PronunciationDrillDto> result = useCase.execute("a1", null);

        assertTrue(result.isEmpty());
    }
}
