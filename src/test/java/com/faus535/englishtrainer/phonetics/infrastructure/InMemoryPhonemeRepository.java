package com.faus535.englishtrainer.phonetics.infrastructure;

import com.faus535.englishtrainer.phonetics.domain.*;

import java.util.*;

public final class InMemoryPhonemeRepository implements PhonemeRepository {
    private final Map<UUID, Phoneme> store = new LinkedHashMap<>();
    private List<Phoneme> uncompletedPhonemes;

    public void addPhoneme(Phoneme phoneme) {
        store.put(phoneme.id().value(), phoneme);
    }

    public void setUncompletedPhonemes(List<Phoneme> phonemes) {
        this.uncompletedPhonemes = phonemes;
    }

    @Override
    public List<Phoneme> findAll() {
        return store.values().stream()
                .sorted(Comparator.comparingInt(Phoneme::difficultyOrder))
                .toList();
    }

    @Override
    public Optional<Phoneme> findById(PhonemeId id) {
        return Optional.ofNullable(store.get(id.value()));
    }

    @Override
    public List<Phoneme> findUncompletedByUserOrderedByDifficulty(UUID userId) {
        return uncompletedPhonemes != null ? uncompletedPhonemes : findAll();
    }
}
