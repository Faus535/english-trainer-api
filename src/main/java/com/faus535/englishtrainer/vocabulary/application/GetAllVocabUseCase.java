package com.faus535.englishtrainer.vocabulary.application;

import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.vocabulary.domain.VocabEntry;
import com.faus535.englishtrainer.vocabulary.domain.VocabRepository;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
public class GetAllVocabUseCase {

    private final VocabRepository repository;

    public GetAllVocabUseCase(VocabRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<VocabEntry> execute() {
        return repository.findAll();
    }
}
