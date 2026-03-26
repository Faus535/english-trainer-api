package com.faus535.englishtrainer.learningpath.infrastructure;

import com.faus535.englishtrainer.learningpath.domain.LearningPath;
import com.faus535.englishtrainer.learningpath.domain.LearningPathId;
import com.faus535.englishtrainer.learningpath.domain.LearningPathRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class InMemoryLearningPathRepository implements LearningPathRepository {

    private final Map<UUID, LearningPath> store = new HashMap<>();

    @Override
    public Optional<LearningPath> findById(LearningPathId id) {
        return Optional.ofNullable(store.get(id.value()));
    }

    @Override
    public Optional<LearningPath> findByUserId(UserProfileId userId) {
        return store.values().stream()
                .filter(p -> p.userId().equals(userId))
                .findFirst();
    }

    @Override
    public LearningPath save(LearningPath learningPath) {
        store.put(learningPath.id().value(), learningPath);
        return learningPath;
    }

    @Override
    public void deleteByUserId(UserProfileId userId) {
        store.values().removeIf(p -> p.userId().equals(userId));
    }
}
