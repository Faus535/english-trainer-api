package com.faus535.englishtrainer.phonetics.infrastructure;

import com.faus535.englishtrainer.phonetics.domain.Phoneme;
import com.faus535.englishtrainer.phonetics.domain.PhonemeId;
import com.faus535.englishtrainer.phonetics.domain.PhonemeRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.util.*;

public final class InMemoryPhonemeRepository implements PhonemeRepository {

    private final Map<UUID, Phoneme> store = new HashMap<>();
    private final Set<UUID> completedPhonemeIds = new HashSet<>();

    @Override
    public List<Phoneme> findAll() {
        return List.copyOf(store.values());
    }

    @Override
    public Optional<Phoneme> findById(PhonemeId id) {
        return Optional.ofNullable(store.get(id.value()));
    }

    @Override
    public List<Phoneme> findUncompletedByUserOrderedByDifficulty(UserProfileId userId) {
        return store.values().stream()
                .filter(p -> !completedPhonemeIds.contains(p.id().value()))
                .sorted(Comparator.comparingInt(Phoneme::difficultyOrder))
                .toList();
    }

    public void addPhoneme(Phoneme phoneme) {
        store.put(phoneme.id().value(), phoneme);
    }

    public void markCompleted(PhonemeId phonemeId) {
        completedPhonemeIds.add(phonemeId.value());
    }
}
