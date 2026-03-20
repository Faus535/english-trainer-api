package com.faus535.englishtrainer.assessment.infrastructure;

import com.faus535.englishtrainer.assessment.domain.LevelTestResult;
import com.faus535.englishtrainer.assessment.domain.LevelTestResultId;
import com.faus535.englishtrainer.assessment.domain.LevelTestResultRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class InMemoryLevelTestResultRepository implements LevelTestResultRepository {

    private final Map<LevelTestResultId, LevelTestResult> store = new HashMap<>();

    @Override
    public List<LevelTestResult> findByUser(UserProfileId userId) {
        return store.values().stream()
                .filter(r -> r.userId().equals(userId))
                .toList();
    }

    @Override
    public LevelTestResult save(LevelTestResult result) {
        store.put(result.id(), result);
        return result;
    }
}
