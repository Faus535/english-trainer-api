package com.faus535.englishtrainer.learningpath.application;

import com.faus535.englishtrainer.activity.infrastructure.InMemoryActivityDateRepository;
import com.faus535.englishtrainer.learningpath.domain.ContentType;
import com.faus535.englishtrainer.learningpath.domain.LearningPath;
import com.faus535.englishtrainer.learningpath.domain.LearningPathId;
import com.faus535.englishtrainer.learningpath.domain.LearningUnit;
import com.faus535.englishtrainer.learningpath.domain.LearningUnitId;
import com.faus535.englishtrainer.learningpath.domain.MasteryScore;
import com.faus535.englishtrainer.learningpath.domain.UnitContent;
import com.faus535.englishtrainer.learningpath.domain.UnitStatus;
import com.faus535.englishtrainer.learningpath.infrastructure.InMemoryLearningPathRepository;
import com.faus535.englishtrainer.learningpath.infrastructure.InMemoryLearningUnitRepository;
import com.faus535.englishtrainer.spacedrepetition.infrastructure.InMemorySpacedRepetitionRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

final class GetLearningStatusUseCaseTest {

    private InMemoryLearningPathRepository learningPathRepository;
    private InMemoryLearningUnitRepository learningUnitRepository;
    private InMemorySpacedRepetitionRepository spacedRepetitionRepository;
    private InMemoryActivityDateRepository activityDateRepository;
    private GetLearningStatusUseCase useCase;

    @BeforeEach
    void setUp() {
        learningPathRepository = new InMemoryLearningPathRepository();
        learningUnitRepository = new InMemoryLearningUnitRepository();
        spacedRepetitionRepository = new InMemorySpacedRepetitionRepository();
        activityDateRepository = new InMemoryActivityDateRepository();

        useCase = new GetLearningStatusUseCase(
                learningPathRepository,
                learningUnitRepository,
                spacedRepetitionRepository,
                activityDateRepository
        );
    }

    @Test
    void shouldReturnFullLearningStatus() {
        UserProfileId userId = UserProfileId.generate();

        LearningUnitId unit0Id = LearningUnitId.generate();
        LearningUnitId unit1Id = LearningUnitId.generate();
        LearningUnitId unit2Id = LearningUnitId.generate();

        LearningPath path = LearningPath.create(userId, "A1", List.of(unit0Id, unit1Id, unit2Id));
        learningPathRepository.save(path);

        LearningUnit unit0 = LearningUnit.reconstitute(
                unit0Id, path.id(), 0, "Greetings", "A1",
                UnitStatus.MASTERED, new MasteryScore(85),
                List.of(
                        new UnitContent(ContentType.VOCAB, UUID.randomUUID(), true, Instant.now()),
                        new UnitContent(ContentType.PHRASE, UUID.randomUUID(), true, Instant.now())
                ),
                Instant.now(), Instant.now(), Instant.now()
        );

        LearningUnit unit1 = LearningUnit.reconstitute(
                unit1Id, path.id(), 1, "Daily Routines", "A1",
                UnitStatus.IN_PROGRESS, new MasteryScore(30),
                List.of(
                        new UnitContent(ContentType.VOCAB, UUID.randomUUID(), true, Instant.now()),
                        new UnitContent(ContentType.PHRASE, UUID.randomUUID(), false, null),
                        new UnitContent(ContentType.GRAMMAR, UUID.randomUUID(), false, null)
                ),
                null, Instant.now(), Instant.now()
        );

        LearningUnit unit2 = LearningUnit.reconstitute(
                unit2Id, path.id(), 2, "Shopping", "A1",
                UnitStatus.NOT_STARTED, new MasteryScore(0),
                List.of(
                        new UnitContent(ContentType.VOCAB, UUID.randomUUID(), false, null)
                ),
                null, Instant.now(), Instant.now()
        );

        learningUnitRepository.save(unit0);
        learningUnitRepository.save(unit1);
        learningUnitRepository.save(unit2);

        // Path starts at unit 0, but unit 0 is mastered, so we need to advance
        // Actually, path.currentUnitIndex() is 0 by default from create
        // Let's simulate current unit being unit 1 (index 1) by reconstituting
        LearningPath advancedPath = LearningPath.reconstitute(
                path.id(), userId, "A1", 1, List.of(unit0Id, unit1Id, unit2Id),
                path.createdAt(), Instant.now()
        );
        learningPathRepository.save(advancedPath);

        GetLearningStatusUseCase.LearningStatus status = useCase.execute(userId);

        assertNotNull(status);

        // Current unit should be unit 1 (Daily Routines)
        assertNotNull(status.currentUnit());
        assertEquals("Daily Routines", status.currentUnit().name());
        assertEquals(1, status.currentUnit().unitIndex());
        assertEquals(30, status.currentUnit().masteryScore());
        assertEquals("IN_PROGRESS", status.currentUnit().status());
        assertEquals(1, status.currentUnit().contentProgress().practiced());
        assertEquals(3, status.currentUnit().contentProgress().total());

        // Next unit should be unit 2 (Shopping)
        assertNotNull(status.nextUnit());
        assertEquals("Shopping", status.nextUnit().name());
        assertEquals(2, status.nextUnit().unitIndex());

        // Overall progress: 1 mastered out of 3
        assertEquals(1, status.overallProgress().unitsCompleted());
        assertEquals(3, status.overallProgress().totalUnits());
        assertEquals(33, status.overallProgress().percentComplete());

        // Today's plan: 2 unpracticed items in current unit
        assertEquals(2, status.todaysPlan().newItemsCount());

        // Weak areas: none (no NEEDS_REVIEW units)
        assertTrue(status.weakAreas().isEmpty());

        // Streak: 0 (no activity dates)
        assertEquals(0, status.currentStreak());
    }

    @Test
    void shouldReturnEmptyStatusWhenNoLearningPathExists() {
        UserProfileId userId = UserProfileId.generate();

        GetLearningStatusUseCase.LearningStatus status = useCase.execute(userId);

        assertNotNull(status);
        assertNull(status.currentUnit());
        assertNull(status.nextUnit());
        assertEquals(0, status.overallProgress().totalUnits());
        assertEquals(0, status.overallProgress().percentComplete());
        assertEquals("not_started", status.todaysPlan().suggestedSessionMode());
        assertTrue(status.weakAreas().isEmpty());
        assertEquals(0, status.currentStreak());
    }

    @Test
    void shouldReturnWeakAreasForNeedsReviewUnits() {
        UserProfileId userId = UserProfileId.generate();

        LearningUnitId unit0Id = LearningUnitId.generate();
        LearningUnitId unit1Id = LearningUnitId.generate();

        LearningPath path = LearningPath.create(userId, "A1", List.of(unit0Id, unit1Id));
        learningPathRepository.save(path);

        LearningUnit unit0 = LearningUnit.reconstitute(
                unit0Id, path.id(), 0, "Greetings", "A1",
                UnitStatus.NEEDS_REVIEW, new MasteryScore(25),
                List.of(new UnitContent(ContentType.VOCAB, UUID.randomUUID(), true, Instant.now())),
                null, Instant.now(), Instant.now()
        );

        LearningUnit unit1 = LearningUnit.reconstitute(
                unit1Id, path.id(), 1, "Daily Routines", "A1",
                UnitStatus.NOT_STARTED, new MasteryScore(0),
                List.of(new UnitContent(ContentType.VOCAB, UUID.randomUUID(), false, null)),
                null, Instant.now(), Instant.now()
        );

        learningUnitRepository.save(unit0);
        learningUnitRepository.save(unit1);

        GetLearningStatusUseCase.LearningStatus status = useCase.execute(userId);

        assertEquals(1, status.weakAreas().size());
        assertEquals("Greetings", status.weakAreas().get(0).unitName());
        assertEquals(25, status.weakAreas().get(0).masteryScore());
    }

    @Test
    void shouldReturnNullNextUnitWhenOnLastUnit() {
        UserProfileId userId = UserProfileId.generate();

        LearningUnitId unit0Id = LearningUnitId.generate();

        LearningPath path = LearningPath.create(userId, "A1", List.of(unit0Id));
        learningPathRepository.save(path);

        LearningUnit unit0 = LearningUnit.reconstitute(
                unit0Id, path.id(), 0, "Greetings", "A1",
                UnitStatus.IN_PROGRESS, new MasteryScore(40),
                List.of(new UnitContent(ContentType.VOCAB, UUID.randomUUID(), false, null)),
                null, Instant.now(), Instant.now()
        );

        learningUnitRepository.save(unit0);

        GetLearningStatusUseCase.LearningStatus status = useCase.execute(userId);

        assertNull(status.nextUnit());
    }
}
