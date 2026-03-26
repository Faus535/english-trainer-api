package com.faus535.englishtrainer.learningpath.application;

import com.faus535.englishtrainer.learningpath.domain.LearningPath;
import com.faus535.englishtrainer.learningpath.domain.LearningPathId;
import com.faus535.englishtrainer.learningpath.domain.LearningPathRepository;
import com.faus535.englishtrainer.learningpath.domain.LearningUnit;
import com.faus535.englishtrainer.learningpath.domain.LearningUnitId;
import com.faus535.englishtrainer.learningpath.domain.LearningUnitRepository;
import com.faus535.englishtrainer.learningpath.domain.UnitStatus;
import com.faus535.englishtrainer.learningpath.domain.error.LearningPathException;
import com.faus535.englishtrainer.learningpath.domain.error.LearningPathNotFoundException;
import com.faus535.englishtrainer.learningpath.domain.error.LearningUnitNotFoundException;
import com.faus535.englishtrainer.learningpath.domain.event.LevelCompletedEvent;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

@UseCase
public class AdvanceUnitUseCase {

    private final LearningPathRepository learningPathRepository;
    private final LearningUnitRepository learningUnitRepository;
    private final ApplicationEventPublisher eventPublisher;

    public AdvanceUnitUseCase(LearningPathRepository learningPathRepository,
                              LearningUnitRepository learningUnitRepository,
                              ApplicationEventPublisher eventPublisher) {
        this.learningPathRepository = learningPathRepository;
        this.learningUnitRepository = learningUnitRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void execute(LearningPathId pathId, LearningUnitId unitId)
            throws LearningPathNotFoundException, LearningUnitNotFoundException, LearningPathException {

        // 1. Load LearningPath
        LearningPath learningPath = learningPathRepository.findById(pathId)
                .orElseThrow(() -> new LearningPathNotFoundException(pathId));

        // 2. Load current LearningUnit and verify it's MASTERED
        LearningUnit currentUnit = learningUnitRepository.findById(unitId)
                .orElseThrow(() -> new LearningUnitNotFoundException(unitId));

        if (currentUnit.status() != UnitStatus.MASTERED) {
            throw new LearningPathException(
                    "Unit must be mastered before advancing. Current status: " + currentUnit.status());
        }

        // 3. Advance LearningPath to next unit
        LearningPath advancedPath = learningPath.advanceToNextUnit();

        // 4. If next unit exists, set to IN_PROGRESS
        if (!advancedPath.isCompleted()) {
            LearningUnitId nextUnitId = advancedPath.currentUnitId();
            LearningUnit nextUnit = learningUnitRepository.findById(nextUnitId)
                    .orElseThrow(() -> new LearningUnitNotFoundException(nextUnitId));

            LearningUnit startedUnit = nextUnit.startUnit();
            learningUnitRepository.save(startedUnit);
        }

        // 5. Save LearningPath
        learningPathRepository.save(advancedPath);

        // 6. If all units mastered, publish LevelCompletedEvent
        if (advancedPath.isCompleted()) {
            eventPublisher.publishEvent(new LevelCompletedEvent(
                    pathId,
                    learningPath.userId(),
                    learningPath.currentLevel()
            ));
        }
    }
}
