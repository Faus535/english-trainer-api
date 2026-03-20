package com.faus535.englishtrainer.vocabulary.application;

import com.faus535.englishtrainer.vocabulary.domain.VocabEntry;
import com.faus535.englishtrainer.vocabulary.domain.VocabEntryMother;
import com.faus535.englishtrainer.vocabulary.domain.VocabLevel;
import com.faus535.englishtrainer.vocabulary.infrastructure.InMemoryVocabRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

final class GetVocabByLevelUseCaseTest {

    private InMemoryVocabRepository repository;
    private GetVocabByLevelUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryVocabRepository();
        useCase = new GetVocabByLevelUseCase(repository);
    }

    @Test
    void shouldReturnEntriesByLevel() {
        repository.save(VocabEntryMother.withLevel("a1"));
        repository.save(VocabEntryMother.withLevel("b2"));

        List<VocabEntry> result = useCase.execute(new VocabLevel("a1"));

        assertEquals(1, result.size());
    }
}
