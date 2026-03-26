package com.faus535.englishtrainer.learningpath.infrastructure.event;

import com.faus535.englishtrainer.assessment.domain.event.LevelTestCompletedEvent;
import com.faus535.englishtrainer.learningpath.application.GenerateLearningPathUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
class LevelTestCompletedListener {

    private static final Logger log = LoggerFactory.getLogger(LevelTestCompletedListener.class);

    private final GenerateLearningPathUseCase generateLearningPathUseCase;

    LevelTestCompletedListener(GenerateLearningPathUseCase generateLearningPathUseCase) {
        this.generateLearningPathUseCase = generateLearningPathUseCase;
    }

    @EventListener
    void handleLevelTestCompleted(LevelTestCompletedEvent event) {
        try {
            var summary = generateLearningPathUseCase.execute(event.userId());
            log.info("Generated learning path for user {} at level {} with {} units",
                    event.userId().value(), summary.level(), summary.unitCount());
        } catch (Exception e) {
            log.error("Error generating learning path for user {}: {}",
                    event.userId().value(), e.getMessage(), e);
        }
    }
}
