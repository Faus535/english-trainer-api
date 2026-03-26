package com.faus535.englishtrainer.learningpath.domain;

import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

final class LearningPathTest {

    @Test
    void shouldCreateLearningPath() {
        UserProfileId userId = UserProfileId.generate();

        LearningPath path = LearningPathMother.create(userId);

        assertNotNull(path.id());
        assertEquals(userId, path.userId());
        assertEquals("A1", path.currentLevel());
        assertEquals(0, path.currentUnitIndex());
        assertEquals(3, path.unitIds().size());
        assertNotNull(path.createdAt());
        assertNotNull(path.updatedAt());
    }

    @Test
    void shouldReturnCurrentUnitId() {
        LearningUnitId firstUnit = LearningUnitId.generate();
        LearningUnitId secondUnit = LearningUnitId.generate();
        LearningPath path = LearningPathMother.create(
                UserProfileId.generate(), "A1", List.of(firstUnit, secondUnit));

        assertEquals(firstUnit, path.currentUnitId());
    }

    @Test
    void shouldAdvanceToNextUnit() {
        LearningPath path = LearningPathMother.create();

        LearningPath advanced = path.advanceToNextUnit();

        assertEquals(1, advanced.currentUnitIndex());
        assertEquals(path.id(), advanced.id());
    }

    @Test
    void shouldBeCompletedWhenAllUnitsFinished() {
        LearningUnitId onlyUnit = LearningUnitId.generate();
        LearningPath path = LearningPathMother.create(
                UserProfileId.generate(), "A1", List.of(onlyUnit));

        assertFalse(path.isCompleted());

        LearningPath advanced = path.advanceToNextUnit();
        assertTrue(advanced.isCompleted());
    }

    @Test
    void shouldReturnNullCurrentUnitIdWhenCompleted() {
        LearningUnitId onlyUnit = LearningUnitId.generate();
        LearningPath path = LearningPathMother.create(
                UserProfileId.generate(), "A1", List.of(onlyUnit));

        LearningPath completed = path.advanceToNextUnit();

        assertNull(completed.currentUnitId());
    }

    @Test
    void shouldHaveImmutableUnitIds() {
        LearningPath path = LearningPathMother.create();

        assertThrows(UnsupportedOperationException.class, () ->
                path.unitIds().add(LearningUnitId.generate()));
    }
}
