package com.faus535.englishtrainer.moduleprogress.infrastructure;

import com.faus535.englishtrainer.moduleprogress.domain.ModuleLevel;
import com.faus535.englishtrainer.moduleprogress.domain.ModuleName;
import com.faus535.englishtrainer.moduleprogress.domain.ModuleProgress;
import com.faus535.englishtrainer.moduleprogress.domain.ModuleProgressId;
import com.faus535.englishtrainer.moduleprogress.domain.ModuleProgressRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class InMemoryModuleProgressRepository implements ModuleProgressRepository {

    private final Map<ModuleProgressId, ModuleProgress> store = new HashMap<>();

    @Override
    public Optional<ModuleProgress> findByUserAndModuleAndLevel(UserProfileId userId, ModuleName moduleName, ModuleLevel level) {
        return store.values().stream()
                .filter(p -> p.userId().equals(userId)
                        && p.moduleName().equals(moduleName)
                        && p.level().equals(level))
                .findFirst();
    }

    @Override
    public List<ModuleProgress> findAllByUser(UserProfileId userId) {
        return store.values().stream()
                .filter(p -> p.userId().equals(userId))
                .toList();
    }

    @Override
    public ModuleProgress save(ModuleProgress progress) {
        store.put(progress.id(), progress);
        return progress;
    }
}
