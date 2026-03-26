package com.faus535.englishtrainer.learningpath.application;

import com.faus535.englishtrainer.learningpath.domain.ContentType;
import com.faus535.englishtrainer.learningpath.domain.LearningPath;
import com.faus535.englishtrainer.learningpath.domain.LearningUnit;
import com.faus535.englishtrainer.learningpath.domain.LearningUnitId;
import com.faus535.englishtrainer.learningpath.domain.MasteryScore;
import com.faus535.englishtrainer.learningpath.domain.UnitContent;
import com.faus535.englishtrainer.learningpath.domain.UnitStatus;
import com.faus535.englishtrainer.learningpath.domain.error.LearningPathNotFoundException;
import com.faus535.englishtrainer.learningpath.infrastructure.InMemoryLearningPathRepository;
import com.faus535.englishtrainer.learningpath.infrastructure.InMemoryLearningUnitRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

final class GetLearningPathUseCaseTest {

    private InMemoryLearningPathRepository learningPathRepository;
    private InMemoryLearningUnitRepository learningUnitRepository;
    private GetLearningPathUseCase useCase;

    @BeforeEach
    void setUp() {
        learningPathRepository = new InMemoryLearningPathRepository();
        learningUnitRepository = new InMemoryLearningUnitRepository();

        useCase = new GetLearningPathUseCase(learningPathRepository, learningUnitRepository);
    }

    @Test
    void shouldReturnLearningPathWithAllUnits() throws Exception {
        UserProfileId userId = UserProfileId.generate();

        LearningUnitId unit0Id = LearningUnitId.generate();
        LearningUnitId unit1Id = LearningUnitId.generate();

        LearningPath path = LearningPath.create(userId, "A1", List.of(unit0Id, unit1Id));
        learningPathRepository.save(path);

        LearningUnit unit0 = LearningUnit.reconstitute(
                unit0Id, path.id(), 0, "Greetings", "A1",
                UnitStatus.MASTERED, new MasteryScore(90),
                List.of(new UnitContent(ContentType.VOCAB, UUID.randomUUID(), true, Instant.now())),
                Instant.now(), Instant.now(), Instant.now()
        );

        LearningUnit unit1 = LearningUnit.reconstitute(
                unit1Id, path.id(), 1, "Daily Routines", "A1",
                UnitStatus.IN_PROGRESS, new MasteryScore(45),
                List.of(new UnitContent(ContentType.VOCAB, UUID.randomUUID(), false, null)),
                null, Instant.now(), Instant.now()
        );

        learningUnitRepository.save(unit0);
        learningUnitRepository.save(unit1);

        GetLearningPathUseCase.LearningPathSummary summary = useCase.execute(userId);

        assertEquals("A1", summary.currentLevel());
        assertEquals(0, summary.currentUnitIndex());
        assertEquals(2, summary.units().size());

        assertEquals(0, summary.units().get(0).unitIndex());
        assertEquals("Greetings", summary.units().get(0).name());
        assertEquals("MASTERED", summary.units().get(0).status());
        assertEquals(90, summary.units().get(0).masteryScore());

        assertEquals(1, summary.units().get(1).unitIndex());
        assertEquals("Daily Routines", summary.units().get(1).name());
        assertEquals("IN_PROGRESS", summary.units().get(1).status());
        assertEquals(45, summary.units().get(1).masteryScore());
    }

    @Test
    void shouldThrowWhenNoLearningPathExists() {
        UserProfileId userId = UserProfileId.generate();

        assertThrows(LearningPathNotFoundException.class, () -> useCase.execute(userId));
    }
}
