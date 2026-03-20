package com.faus535.englishtrainer.vocabulary.application;

import com.faus535.englishtrainer.shared.domain.annotation.UseCase;
import com.faus535.englishtrainer.vocabulary.domain.VocabEntry;
import com.faus535.englishtrainer.vocabulary.domain.VocabRepository;

import java.util.List;

@UseCase
public final class GetAllVocabUseCase {

    private final VocabRepository repository;

    public GetAllVocabUseCase(VocabRepository repository) {
        this.repository = repository;
    }

    public List<VocabEntry> execute() {
        return repository.findAll();
    }
}
