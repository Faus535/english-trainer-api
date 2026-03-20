package com.faus535.englishtrainer.phrase.application;

import com.faus535.englishtrainer.phrase.domain.Phrase;
import com.faus535.englishtrainer.phrase.domain.PhraseRepository;
import com.faus535.englishtrainer.shared.domain.annotation.UseCase;
import com.faus535.englishtrainer.vocabulary.domain.VocabLevel;

import java.util.List;

@UseCase
public final class GetRandomPhrasesUseCase {

    private final PhraseRepository repository;

    public GetRandomPhrasesUseCase(PhraseRepository repository) {
        this.repository = repository;
    }

    public List<Phrase> execute(int count, VocabLevel level) {
        return repository.findRandom(count, level);
    }
}
