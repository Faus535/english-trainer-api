package com.faus535.englishtrainer.vocabulary.application;

import com.faus535.englishtrainer.shared.domain.annotation.UseCase;
import com.faus535.englishtrainer.vocabulary.domain.VocabEntry;
import com.faus535.englishtrainer.vocabulary.domain.VocabLevel;
import com.faus535.englishtrainer.vocabulary.domain.VocabRepository;

import java.util.List;

@UseCase
public final class GetRandomVocabUseCase {

    private final VocabRepository repository;

    public GetRandomVocabUseCase(VocabRepository repository) {
        this.repository = repository;
    }

    public List<VocabEntry> execute(int count, VocabLevel level) {
        return repository.findRandom(count, level);
    }
}
