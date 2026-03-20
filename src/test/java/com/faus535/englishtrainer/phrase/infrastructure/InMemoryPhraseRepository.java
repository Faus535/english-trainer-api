package com.faus535.englishtrainer.phrase.infrastructure;

import com.faus535.englishtrainer.phrase.domain.Phrase;
import com.faus535.englishtrainer.phrase.domain.PhraseId;
import com.faus535.englishtrainer.phrase.domain.PhraseRepository;
import com.faus535.englishtrainer.vocabulary.domain.VocabLevel;

import java.util.*;
import java.util.stream.Collectors;

public final class InMemoryPhraseRepository implements PhraseRepository {

    private final Map<PhraseId, Phrase> store = new HashMap<>();

    @Override
    public List<Phrase> findByLevel(VocabLevel level) {
        return store.values().stream()
                .filter(phrase -> phrase.level().equals(level))
                .collect(Collectors.toList());
    }

    @Override
    public List<Phrase> findRandom(int count, VocabLevel level) {
        List<Phrase> byLevel = findByLevel(level);
        Collections.shuffle(byLevel);
        return byLevel.stream().limit(count).collect(Collectors.toList());
    }

    @Override
    public Phrase save(Phrase phrase) {
        store.put(phrase.id(), phrase);
        return phrase;
    }

    public int count() {
        return store.size();
    }

    public void clear() {
        store.clear();
    }
}
