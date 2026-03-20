package com.faus535.englishtrainer.curriculum.application;

import com.faus535.englishtrainer.curriculum.domain.CurriculumBlock;
import com.faus535.englishtrainer.curriculum.domain.CurriculumProvider;
import com.faus535.englishtrainer.shared.domain.annotation.UseCase;

import java.util.List;
import java.util.Optional;

@UseCase
public final class GetCurriculumPlanUseCase {

    private final CurriculumProvider curriculumProvider;

    public GetCurriculumPlanUseCase(CurriculumProvider curriculumProvider) {
        this.curriculumProvider = curriculumProvider;
    }

    public List<CurriculumBlock> execute() {
        return curriculumProvider.getPlan();
    }

    public Optional<CurriculumBlock> execute(int blockNumber) {
        return curriculumProvider.getBlock(blockNumber);
    }
}
