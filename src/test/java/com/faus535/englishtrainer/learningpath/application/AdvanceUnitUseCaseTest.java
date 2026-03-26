package com.faus535.englishtrainer.learningpath.application;

import com.faus535.englishtrainer.learningpath.domain.ContentType;
import com.faus535.englishtrainer.learningpath.domain.LearningPath;
import com.faus535.englishtrainer.learningpath.domain.LearningUnit;
import com.faus535.englishtrainer.learningpath.domain.LearningUnitId;
import com.faus535.englishtrainer.learningpath.domain.MasteryScore;
import com.faus535.englishtrainer.learningpath.domain.UnitContent;
import com.faus535.englishtrainer.learningpath.domain.UnitStatus;
import com.faus535.englishtrainer.learningpath.domain.error.LearningPathException;
import com.faus535.englishtrainer.learningpath.domain.event.LevelCompletedEvent;
import com.faus535.englishtrainer.learningpath.infrastructure.InMemoryLearningPathRepository;
import com.faus535.englishtrainer.learningpath.infrastructure.InMemoryLearningUnitRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdvanceUnitUseCaseTest {

    private InMemoryLearningPathRepository learningPathRepository;
    private InMemoryLearningUnitRepository learningUnitRepository;
    private List<Object> publishedEvents;
    private AdvanceUnitUseCase useCase;

    @BeforeEach
    void setUp() {
        learningPathRepository = new InMemoryLearningPathRepository();
        learningUnitRepository = new InMemoryLearningUnitRepository();
        publishedEvents = new ArrayList<>();
        ApplicationEventPublisher eventPublisher = publishedEvents::add;
        useCase = new AdvanceUnitUseCase(learningPathRepository, learningUnitRepository, eventPublisher);
    }

    @Test
    void should_advance_to_next_unit() throws Exception {
        UserProfileId userId = UserProfileId.generate();

        LearningUnitId unit1Id = LearningUnitId.generate();
        LearningUnitId unit2Id = LearningUnitId.generate();

        LearningPath path = LearningPath.create(userId, "A1", List.of(unit1Id, unit2Id));
        learningPathRepository.save(path);

        // Unit 1 is mastered
        LearningUnit unit1 = createMasteredUnit(path, unit1Id, 0, "Greetings");
        learningUnitRepository.save(unit1);

        // Unit 2 is not started
        LearningUnit unit2 = LearningUnit.reconstitute(
                unit2Id, path.id(), 1, "Numbers", "A1",
                UnitStatus.NOT_STARTED, new MasteryScore(0),
                List.of(new UnitContent(ContentType.VOCAB, UUID.randomUUID(), false, null)),
                null, null, null
        );
        learningUnitRepository.save(unit2);

        // Execute
        useCase.execute(path.id(), unit1Id);

        // Verify path advanced
        LearningPath updatedPath = learningPathRepository.findById(path.id()).orElseThrow();
        assertEquals(1, updatedPath.currentUnitIndex());

        // Verify next unit is IN_PROGRESS
        LearningUnit updatedUnit2 = learningUnitRepository.findById(unit2Id).orElseThrow();
        assertEquals(UnitStatus.IN_PROGRESS, updatedUnit2.status());

        // No LevelCompletedEvent
        assertTrue(publishedEvents.isEmpty());
    }

    @Test
    void should_publish_level_completed_event_when_all_units_mastered() throws Exception {
        UserProfileId userId = UserProfileId.generate();

        LearningUnitId unit1Id = LearningUnitId.generate();

        LearningPath path = LearningPath.create(userId, "A1", List.of(unit1Id));
        learningPathRepository.save(path);

        LearningUnit unit1 = createMasteredUnit(path, unit1Id, 0, "Greetings");
        learningUnitRepository.save(unit1);

        // Execute — last unit
        useCase.execute(path.id(), unit1Id);

        // Verify path is completed
        LearningPath updatedPath = learningPathRepository.findById(path.id()).orElseThrow();
        assertTrue(updatedPath.isCompleted());

        // Verify event published
        assertEquals(1, publishedEvents.size());
        assertTrue(publishedEvents.get(0) instanceof LevelCompletedEvent);
        LevelCompletedEvent event = (LevelCompletedEvent) publishedEvents.get(0);
        assertEquals("A1", event.level());
        assertEquals(userId, event.userId());
    }

    @Test
    void should_throw_when_unit_not_mastered() {
        UserProfileId userId = UserProfileId.generate();
        LearningUnitId unitId = LearningUnitId.generate();

        LearningPath path = LearningPath.create(userId, "A1", List.of(unitId));
        learningPathRepository.save(path);

        // Unit is IN_PROGRESS, not mastered
        LearningUnit unit = LearningUnit.reconstitute(
                unitId, path.id(), 0, "Greetings", "A1",
                UnitStatus.IN_PROGRESS, new MasteryScore(30),
                List.of(new UnitContent(ContentType.VOCAB, UUID.randomUUID(), false, null)),
                null, null, null
        );
        learningUnitRepository.save(unit);

        assertThrows(LearningPathException.class, () ->
                useCase.execute(path.id(), unitId));
    }

    private LearningUnit createMasteredUnit(LearningPath path, LearningUnitId unitId,
                                             int index, String name) {
        LearningUnit unit = LearningUnit.reconstitute(
                unitId, path.id(), index, name, "A1",
                UnitStatus.MASTERED, new MasteryScore(85),
                List.of(new UnitContent(ContentType.VOCAB, UUID.randomUUID(), true, null)),
                null, null, null
        );
        return unit;
    }
}
