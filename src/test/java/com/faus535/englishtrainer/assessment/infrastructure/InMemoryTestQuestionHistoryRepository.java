package com.faus535.englishtrainer.assessment.infrastructure;

import com.faus535.englishtrainer.assessment.domain.TestQuestionHistoryRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class InMemoryTestQuestionHistoryRepository implements TestQuestionHistoryRepository {

    private final Map<UUID, List<UUID>> store = new HashMap<>();

    @Override
    public List<UUID> findQuestionIdsByUser(UserProfileId userId) {
        return store.getOrDefault(userId.value(), List.of());
    }

    @Override
    public void saveAll(UserProfileId userId, List<UUID> questionIds) {
        store.computeIfAbsent(userId.value(), k -> new ArrayList<>()).addAll(questionIds);
    }
}
