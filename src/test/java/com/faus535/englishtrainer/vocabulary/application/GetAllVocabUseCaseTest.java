package com.faus535.englishtrainer.vocabulary.application;

import com.faus535.englishtrainer.vocabulary.domain.VocabEntry;
import com.faus535.englishtrainer.vocabulary.domain.VocabEntryMother;
import com.faus535.englishtrainer.vocabulary.infrastructure.InMemoryVocabRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

final class GetAllVocabUseCaseTest {

    private InMemoryVocabRepository repository;
    private GetAllVocabUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryVocabRepository();
        useCase = new GetAllVocabUseCase(repository);
    }

    @Test
    void shouldReturnAllEntries() {
        repository.save(VocabEntryMother.create());
        repository.save(VocabEntryMother.create());

        List<VocabEntry> result = useCase.execute();

        assertEquals(2, result.size());
    }

    @Test
    void shouldReturnEmptyWhenNoEntries() {
        List<VocabEntry> result = useCase.execute();

        assertTrue(result.isEmpty());
    }
}
