package com.faus535.englishtrainer.phrase.domain;

import com.faus535.englishtrainer.vocabulary.domain.VocabLevel;

import java.util.List;

public interface PhraseRepository {

    List<Phrase> findByLevel(VocabLevel level);

    List<Phrase> findRandom(int count, VocabLevel level);

    Phrase save(Phrase phrase);
}
