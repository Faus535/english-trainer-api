package com.faus535.englishtrainer.phrase.application;

import com.faus535.englishtrainer.phrase.domain.Phrase;
import com.faus535.englishtrainer.phrase.domain.PhraseRepository;
import com.faus535.englishtrainer.shared.domain.annotation.UseCase;
import com.faus535.englishtrainer.vocabulary.domain.VocabLevel;

import java.util.List;

@UseCase
public final class GetPhrasesByLevelUseCase {

    private final PhraseRepository repository;

    public GetPhrasesByLevelUseCase(PhraseRepository repository) {
        this.repository = repository;
    }

    public List<Phrase> execute(VocabLevel level) {
        return repository.findByLevel(level);
    }
}
