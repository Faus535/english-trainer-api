package com.faus535.englishtrainer.phonetics.infrastructure;

import com.faus535.englishtrainer.phonetics.domain.*;

import java.util.*;

public final class InMemoryPhonemePracticePhraseRepository implements PhonemePracticePhraseRepository {
    private final List<PhonemePracticePhrase> store = new ArrayList<>();

    public void addPhrase(PhonemePracticePhrase phrase) {
        store.add(phrase);
    }

    public void addAll(List<PhonemePracticePhrase> phrases) {
        store.addAll(phrases);
    }

    @Override
    public List<PhonemePracticePhrase> findByPhonemeId(PhonemeId phonemeId) {
        return store.stream()
                .filter(p -> p.phonemeId().equals(phonemeId))
                .toList();
    }
}
