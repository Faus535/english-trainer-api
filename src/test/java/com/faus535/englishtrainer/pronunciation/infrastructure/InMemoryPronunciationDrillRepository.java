package com.faus535.englishtrainer.pronunciation.infrastructure;

import com.faus535.englishtrainer.pronunciation.domain.PronunciationDrill;
import com.faus535.englishtrainer.pronunciation.domain.PronunciationDrillId;
import com.faus535.englishtrainer.pronunciation.domain.PronunciationDrillRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryPronunciationDrillRepository implements PronunciationDrillRepository {

    private final Map<PronunciationDrillId, PronunciationDrill> store = new HashMap<>();

    public void add(PronunciationDrill drill) {
        store.put(drill.id(), drill);
    }

    public void clear() {
        store.clear();
    }

    public int count() {
        return store.size();
    }

    @Override
    public Optional<PronunciationDrill> findById(PronunciationDrillId id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<PronunciationDrill> findByLevel(String cefrLevel) {
        return store.values().stream()
                .filter(d -> d.cefrLevel().equalsIgnoreCase(cefrLevel))
                .toList();
    }

    @Override
    public List<PronunciationDrill> findByLevelAndFocus(String cefrLevel, String focus) {
        return store.values().stream()
                .filter(d -> d.cefrLevel().equalsIgnoreCase(cefrLevel)
                        && d.focus().equalsIgnoreCase(focus))
                .toList();
    }

    @Override
    public PronunciationDrill save(PronunciationDrill drill) {
        store.put(drill.id(), drill);
        return drill;
    }
}
