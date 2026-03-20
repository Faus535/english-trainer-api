package com.faus535.englishtrainer.vocabulary.application;

import com.faus535.englishtrainer.shared.domain.annotation.UseCase;
import com.faus535.englishtrainer.vocabulary.domain.VocabEntry;
import com.faus535.englishtrainer.vocabulary.domain.VocabRepository;

import java.util.List;

@UseCase
public final class SearchVocabUseCase {

    private final VocabRepository repository;

    public SearchVocabUseCase(VocabRepository repository) {
        this.repository = repository;
    }

    public List<VocabEntry> execute(String query) {
        return repository.searchByWord(query);
    }
}
