package com.faus535.englishtrainer.moduleprogress.application;

import com.faus535.englishtrainer.moduleprogress.domain.ModuleLevel;
import com.faus535.englishtrainer.moduleprogress.domain.ModuleName;
import com.faus535.englishtrainer.moduleprogress.domain.ModuleProgress;
import com.faus535.englishtrainer.moduleprogress.domain.ModuleProgressRepository;
import com.faus535.englishtrainer.moduleprogress.domain.error.ModuleProgressNotFoundException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.transaction.annotation.Transactional;

@UseCase
public class CheckLevelUpUseCase {

    private static final int REQUIRED_UNITS = 10;

    private final ModuleProgressRepository repository;

    public CheckLevelUpUseCase(ModuleProgressRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public LevelUpResult execute(UserProfileId userId, ModuleName moduleName, ModuleLevel level)
            throws ModuleProgressNotFoundException {
        ModuleProgress progress = repository.findByUserAndModuleAndLevel(userId, moduleName, level)
                .orElseThrow(() -> new ModuleProgressNotFoundException(moduleName.value(), level.value()));

        boolean eligible = progress.completedUnits().size() >= REQUIRED_UNITS;
        return new LevelUpResult(eligible, level.value(), progress.completedUnits().size(), REQUIRED_UNITS);
    }

    public record LevelUpResult(boolean eligible, String currentLevel, int completedUnits, int requiredUnits) {}
}
