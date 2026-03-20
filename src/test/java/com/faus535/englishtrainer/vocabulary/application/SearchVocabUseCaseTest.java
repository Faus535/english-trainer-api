package com.faus535.englishtrainer.vocabulary.application;

import com.faus535.englishtrainer.vocabulary.domain.VocabEntry;
import com.faus535.englishtrainer.vocabulary.domain.VocabEntryId;
import com.faus535.englishtrainer.vocabulary.domain.VocabLevel;
import com.faus535.englishtrainer.vocabulary.infrastructure.InMemoryVocabRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

final class SearchVocabUseCaseTest {

    private InMemoryVocabRepository repository;
    private SearchVocabUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryVocabRepository();
        useCase = new SearchVocabUseCase(repository);
    }

    @Test
    void shouldReturnMatchingEntries() {
        repository.save(VocabEntry.create(VocabEntryId.generate(), "hello", "/həˈloʊ/", "hola", "noun", "Hello there", new VocabLevel("a1")));
        repository.save(VocabEntry.create(VocabEntryId.generate(), "goodbye", "/ɡʊdˈbaɪ/", "adiós", "noun", "Goodbye!", new VocabLevel("a1")));

        List<VocabEntry> result = useCase.execute("hello");

        assertEquals(1, result.size());
        assertEquals("hello", result.get(0).en());
    }
}
