package com.faus535.englishtrainer.vocabulary.application;

import com.faus535.englishtrainer.vocabulary.domain.VocabEntry;
import com.faus535.englishtrainer.vocabulary.domain.VocabRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetVocabByLevelUseCase {

    private final VocabRepository repository;

    public GetVocabByLevelUseCase(VocabRepository repository) {
        this.repository = repository;
    }

    public List<VocabEntry> execute(String level) {
        return repository.findByLevel(level);
    }
}
