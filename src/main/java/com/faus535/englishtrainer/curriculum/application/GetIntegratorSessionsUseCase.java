package com.faus535.englishtrainer.curriculum.application;

import com.faus535.englishtrainer.curriculum.domain.CurriculumProvider;
import com.faus535.englishtrainer.curriculum.domain.IntegratorDefinition;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
public class GetIntegratorSessionsUseCase {

    private final CurriculumProvider curriculumProvider;

    public GetIntegratorSessionsUseCase(CurriculumProvider curriculumProvider) {
        this.curriculumProvider = curriculumProvider;
    }

    @Transactional(readOnly = true)
    public List<IntegratorDefinition> execute(String level) {
        return curriculumProvider.getIntegrators(level);
    }

    @Transactional(readOnly = true)
    public List<IntegratorDefinition> execute() {
        return curriculumProvider.getAllIntegrators();
    }
}
