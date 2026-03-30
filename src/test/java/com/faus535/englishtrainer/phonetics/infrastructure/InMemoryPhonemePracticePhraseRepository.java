package com.faus535.englishtrainer.phonetics.infrastructure;

import com.faus535.englishtrainer.phonetics.domain.PhonemeId;
import com.faus535.englishtrainer.phonetics.domain.PhonemePracticePhrase;
import com.faus535.englishtrainer.phonetics.domain.PhonemePracticePhraseRepository;

import java.util.*;

public final class InMemoryPhonemePracticePhraseRepository implements PhonemePracticePhraseRepository {

    private final Map<UUID, PhonemePracticePhrase> store = new HashMap<>();

    @Override
    public List<PhonemePracticePhrase> findByPhonemeId(PhonemeId phonemeId) {
        return store.values().stream()
                .filter(p -> p.phonemeId().equals(phonemeId))
                .toList();
    }

    public void addAll(List<PhonemePracticePhrase> phrases) {
        phrases.forEach(p -> store.put(p.id().value(), p));
    }
}
