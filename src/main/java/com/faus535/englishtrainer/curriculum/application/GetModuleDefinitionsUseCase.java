package com.faus535.englishtrainer.curriculum.application;

import com.faus535.englishtrainer.curriculum.domain.CurriculumProvider;
import com.faus535.englishtrainer.curriculum.domain.ModuleDefinition;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@UseCase
public class GetModuleDefinitionsUseCase {

    private final CurriculumProvider curriculumProvider;

    public GetModuleDefinitionsUseCase(CurriculumProvider curriculumProvider) {
        this.curriculumProvider = curriculumProvider;
    }

    @Transactional(readOnly = true)
    public List<ModuleDefinition> execute() {
        return curriculumProvider.getModules();
    }

    @Transactional(readOnly = true)
    public Optional<ModuleDefinition> execute(String moduleName) {
        return curriculumProvider.getModule(moduleName);
    }
}
