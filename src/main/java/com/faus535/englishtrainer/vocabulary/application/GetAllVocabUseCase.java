package com.faus535.englishtrainer.vocabulary.application;

import com.faus535.englishtrainer.vocabulary.domain.VocabEntry;
import com.faus535.englishtrainer.vocabulary.domain.VocabRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllVocabUseCase {

    private final VocabRepository repository;

    public GetAllVocabUseCase(VocabRepository repository) {
        this.repository = repository;
    }

    public List<VocabEntry> execute() {
        return repository.findAll();
    }
}
