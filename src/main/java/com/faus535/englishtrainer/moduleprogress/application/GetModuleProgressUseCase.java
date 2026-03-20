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
public class GetModuleProgressUseCase {

    private final ModuleProgressRepository repository;

    public GetModuleProgressUseCase(ModuleProgressRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public ModuleProgress execute(UserProfileId userId, ModuleName moduleName, ModuleLevel level)
            throws ModuleProgressNotFoundException {
        return repository.findByUserAndModuleAndLevel(userId, moduleName, level)
                .orElseThrow(() -> new ModuleProgressNotFoundException(moduleName.value(), level.value()));
    }
}
