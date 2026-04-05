package com.faus535.englishtrainer.immerse.infrastructure;

import com.faus535.englishtrainer.immerse.domain.*;

import java.util.*;

public class InMemoryImmerseExerciseRepository implements ImmerseExerciseRepository {

    private final Map<UUID, ImmerseExercise> store = new LinkedHashMap<>();

    @Override
    public void saveAll(List<ImmerseExercise> exercises) {
        exercises.forEach(e -> store.put(e.id().value(), e));
    }

    @Override
    public List<ImmerseExercise> findByContentId(ImmerseContentId contentId) {
        return store.values().stream()
                .filter(e -> e.contentId().equals(contentId))
                .sorted(Comparator.comparingInt(ImmerseExercise::orderIndex))
                .toList();
    }

    @Override
    public Optional<ImmerseExercise> findById(ImmerseExerciseId id) {
        return Optional.ofNullable(store.get(id.value()));
    }
}
