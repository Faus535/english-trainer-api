package com.faus535.englishtrainer.curriculum.application;

import com.faus535.englishtrainer.curriculum.domain.CurriculumProvider;
import com.faus535.englishtrainer.curriculum.domain.IntegratorDefinition;
import com.faus535.englishtrainer.shared.domain.annotation.UseCase;

import java.util.List;

@UseCase
public final class GetIntegratorSessionsUseCase {

    private final CurriculumProvider curriculumProvider;

    public GetIntegratorSessionsUseCase(CurriculumProvider curriculumProvider) {
        this.curriculumProvider = curriculumProvider;
    }

    public List<IntegratorDefinition> execute(String level) {
        return curriculumProvider.getIntegrators(level);
    }

    public List<IntegratorDefinition> execute() {
        return curriculumProvider.getAllIntegrators();
    }
}
