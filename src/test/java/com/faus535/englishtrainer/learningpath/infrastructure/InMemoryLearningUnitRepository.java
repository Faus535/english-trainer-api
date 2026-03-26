package com.faus535.englishtrainer.learningpath.infrastructure;

import com.faus535.englishtrainer.learningpath.domain.LearningPathId;
import com.faus535.englishtrainer.learningpath.domain.LearningUnit;
import com.faus535.englishtrainer.learningpath.domain.LearningUnitId;
import com.faus535.englishtrainer.learningpath.domain.LearningUnitRepository;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class InMemoryLearningUnitRepository implements LearningUnitRepository {

    private final Map<UUID, LearningUnit> store = new HashMap<>();

    @Override
    public Optional<LearningUnit> findById(LearningUnitId id) {
        return Optional.ofNullable(store.get(id.value()));
    }

    @Override
    public List<LearningUnit> findByLearningPathId(LearningPathId learningPathId) {
        return store.values().stream()
                .filter(u -> u.learningPathId().equals(learningPathId))
                .sorted(Comparator.comparingInt(LearningUnit::unitIndex))
                .toList();
    }

    @Override
    public LearningUnit save(LearningUnit learningUnit) {
        store.put(learningUnit.id().value(), learningUnit);
        return learningUnit;
    }

    @Override
    public void saveAll(List<LearningUnit> learningUnits) {
        learningUnits.forEach(u -> store.put(u.id().value(), u));
    }
}
