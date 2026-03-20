package com.faus535.englishtrainer.vocabulary.infrastructure;

import com.faus535.englishtrainer.vocabulary.domain.VocabEntry;
import com.faus535.englishtrainer.vocabulary.domain.VocabEntryId;
import com.faus535.englishtrainer.vocabulary.domain.VocabLevel;
import com.faus535.englishtrainer.vocabulary.domain.VocabRepository;

import java.util.*;
import java.util.stream.Collectors;

public final class InMemoryVocabRepository implements VocabRepository {

    private final Map<VocabEntryId, VocabEntry> store = new HashMap<>();

    @Override
    public List<VocabEntry> findAll() {
        return List.copyOf(store.values());
    }

    @Override
    public List<VocabEntry> findByLevel(VocabLevel level) {
        return store.values().stream()
                .filter(entry -> entry.level().equals(level))
                .collect(Collectors.toList());
    }

    @Override
    public List<VocabEntry> searchByWord(String query) {
        return store.values().stream()
                .filter(entry -> entry.en().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public VocabEntry save(VocabEntry entry) {
        store.put(entry.id(), entry);
        return entry;
    }

    @Override
    public List<VocabEntry> findRandom(int count, VocabLevel level) {
        List<VocabEntry> byLevel = findByLevel(level);
        Collections.shuffle(byLevel);
        return byLevel.stream().limit(count).collect(Collectors.toList());
    }

    public int count() {
        return store.size();
    }

    public void clear() {
        store.clear();
    }
}
