package com.faus535.englishtrainer.assessment.infrastructure;

import com.faus535.englishtrainer.assessment.domain.MiniTestResult;
import com.faus535.englishtrainer.assessment.domain.MiniTestResultId;
import com.faus535.englishtrainer.assessment.domain.MiniTestResultRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class InMemoryMiniTestResultRepository implements MiniTestResultRepository {

    private final Map<MiniTestResultId, MiniTestResult> store = new HashMap<>();

    @Override
    public List<MiniTestResult> findByUser(UserProfileId userId) {
        return store.values().stream()
                .filter(r -> r.userId().equals(userId))
                .toList();
    }

    @Override
    public List<MiniTestResult> findByUserAndModule(UserProfileId userId, String moduleName) {
        return store.values().stream()
                .filter(r -> r.userId().equals(userId) && r.moduleName().equals(moduleName))
                .toList();
    }

    @Override
    public MiniTestResult save(MiniTestResult result) {
        store.put(result.id(), result);
        return result;
    }
}
