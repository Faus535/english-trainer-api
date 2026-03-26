package com.faus535.englishtrainer.learningpath.application;

import com.faus535.englishtrainer.learningpath.domain.LearningPath;
import com.faus535.englishtrainer.learningpath.domain.LearningPathRepository;
import com.faus535.englishtrainer.learningpath.domain.LearningUnit;
import com.faus535.englishtrainer.learningpath.domain.LearningUnitRepository;
import com.faus535.englishtrainer.learningpath.domain.error.LearningPathNotFoundException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
public class GetLearningPathUseCase {

    private final LearningPathRepository learningPathRepository;
    private final LearningUnitRepository learningUnitRepository;

    public GetLearningPathUseCase(LearningPathRepository learningPathRepository,
                                   LearningUnitRepository learningUnitRepository) {
        this.learningPathRepository = learningPathRepository;
        this.learningUnitRepository = learningUnitRepository;
    }

    public record UnitInfo(int unitIndex, String name, String status, int masteryScore) {}

    public record LearningPathSummary(String currentLevel, int currentUnitIndex,
                                       List<UnitInfo> units) {}

    @Transactional(readOnly = true)
    public LearningPathSummary execute(UserProfileId userId) throws LearningPathNotFoundException {
        LearningPath path = learningPathRepository.findByUserId(userId)
                .orElseThrow(() -> new LearningPathNotFoundException(userId));

        List<LearningUnit> allUnits = learningUnitRepository.findByLearningPathId(path.id());

        List<UnitInfo> units = allUnits.stream()
                .map(u -> new UnitInfo(
                        u.unitIndex(),
                        u.unitName(),
                        u.status().name(),
                        u.masteryScore().value()
                ))
                .toList();

        return new LearningPathSummary(path.currentLevel(), path.currentUnitIndex(), units);
    }
}
