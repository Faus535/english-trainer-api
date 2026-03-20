package com.faus535.englishtrainer.moduleprogress.application;

import com.faus535.englishtrainer.moduleprogress.domain.ModuleLevel;
import com.faus535.englishtrainer.moduleprogress.domain.ModuleName;
import com.faus535.englishtrainer.moduleprogress.domain.ModuleProgress;
import com.faus535.englishtrainer.moduleprogress.domain.ModuleProgressRepository;
import com.faus535.englishtrainer.shared.domain.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;

@UseCase
public final class InitModuleProgressUseCase {

    private final ModuleProgressRepository repository;

    public InitModuleProgressUseCase(ModuleProgressRepository repository) {
        this.repository = repository;
    }

    public ModuleProgress execute(UserProfileId userId, ModuleName moduleName, ModuleLevel level) {
        ModuleProgress progress = ModuleProgress.create(userId, moduleName, level);
        return repository.save(progress);
    }
}
