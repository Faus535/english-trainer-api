package com.faus535.englishtrainer.curriculum.application;

import com.faus535.englishtrainer.curriculum.domain.CurriculumProvider;
import com.faus535.englishtrainer.curriculum.domain.ModuleDefinition;
import com.faus535.englishtrainer.shared.domain.annotation.UseCase;

import java.util.List;
import java.util.Optional;

@UseCase
public final class GetModuleDefinitionsUseCase {

    private final CurriculumProvider curriculumProvider;

    public GetModuleDefinitionsUseCase(CurriculumProvider curriculumProvider) {
        this.curriculumProvider = curriculumProvider;
    }

    public List<ModuleDefinition> execute() {
        return curriculumProvider.getModules();
    }

    public Optional<ModuleDefinition> execute(String moduleName) {
        return curriculumProvider.getModule(moduleName);
    }
}
