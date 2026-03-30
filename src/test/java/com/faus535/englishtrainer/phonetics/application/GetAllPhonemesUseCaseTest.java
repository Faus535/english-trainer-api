package com.faus535.englishtrainer.phonetics.application;

import com.faus535.englishtrainer.phonetics.domain.*;
import com.faus535.englishtrainer.phonetics.infrastructure.InMemoryPhonemeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

final class GetAllPhonemesUseCaseTest {
    private InMemoryPhonemeRepository repository;
    private GetAllPhonemesUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryPhonemeRepository();
        useCase = new GetAllPhonemesUseCase(repository);
    }

    @Test
    void shouldReturnAllPhonemesOrderedByDifficulty() {
        PhonemeId id1 = new PhonemeId(UUID.randomUUID());
        PhonemeId id2 = new PhonemeId(UUID.randomUUID());
        repository.addPhoneme(PhonemeMother.withIdAndOrder(id2, 2));
        repository.addPhoneme(PhonemeMother.withIdAndOrder(id1, 1));

        List<Phoneme> result = useCase.execute();

        assertEquals(2, result.size());
        assertEquals(id1, result.get(0).id());
        assertEquals(id2, result.get(1).id());
    }

    @Test
    void shouldReturnEmptyListWhenNoPhonemesExist() {
        List<Phoneme> result = useCase.execute();
        assertTrue(result.isEmpty());
    }
}
