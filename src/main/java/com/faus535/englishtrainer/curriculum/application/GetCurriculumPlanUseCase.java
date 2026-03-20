package com.faus535.englishtrainer.curriculum.application;

import com.faus535.englishtrainer.curriculum.domain.CurriculumBlock;
import com.faus535.englishtrainer.curriculum.domain.CurriculumProvider;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@UseCase
public class GetCurriculumPlanUseCase {

    private final CurriculumProvider curriculumProvider;

    public GetCurriculumPlanUseCase(CurriculumProvider curriculumProvider) {
        this.curriculumProvider = curriculumProvider;
    }

    @Transactional(readOnly = true)
    public List<CurriculumBlock> execute() {
        return curriculumProvider.getPlan();
    }

    @Transactional(readOnly = true)
    public Optional<CurriculumBlock> execute(int blockNumber) {
        return curriculumProvider.getBlock(blockNumber);
    }
}
