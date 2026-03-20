package com.faus535.englishtrainer.moduleprogress.application;

import com.faus535.englishtrainer.moduleprogress.domain.ModuleLevel;
import com.faus535.englishtrainer.moduleprogress.domain.ModuleName;
import com.faus535.englishtrainer.moduleprogress.domain.ModuleProgress;
import com.faus535.englishtrainer.moduleprogress.domain.ModuleProgressRepository;
import com.faus535.englishtrainer.moduleprogress.domain.error.ModuleProgressNotFoundException;
import com.faus535.englishtrainer.shared.domain.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;

@UseCase
public final class CompleteUnitUseCase {

    private final ModuleProgressRepository repository;

    public CompleteUnitUseCase(ModuleProgressRepository repository) {
        this.repository = repository;
    }

    public ModuleProgress execute(UserProfileId userId, ModuleName moduleName, ModuleLevel level,
                                  int unitIndex, int score) throws ModuleProgressNotFoundException {
        ModuleProgress progress = repository.findByUserAndModuleAndLevel(userId, moduleName, level)
                .orElseThrow(() -> new ModuleProgressNotFoundException(moduleName.value(), level.value()));

        ModuleProgress updated = progress.completeUnit(unitIndex, score);
        return repository.save(updated);
    }
}
