package com.faus535.englishtrainer.phrase.application;

import com.faus535.englishtrainer.phrase.domain.Phrase;
import com.faus535.englishtrainer.phrase.domain.PhraseRepository;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.vocabulary.domain.VocabLevel;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
public class GetPhrasesByLevelUseCase {

    private final PhraseRepository repository;

    public GetPhrasesByLevelUseCase(PhraseRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Phrase> execute(VocabLevel level) {
        return repository.findByLevel(level);
    }
}
