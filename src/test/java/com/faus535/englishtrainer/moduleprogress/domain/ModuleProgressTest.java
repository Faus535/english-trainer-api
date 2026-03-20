package com.faus535.englishtrainer.moduleprogress.domain;

import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModuleProgressTest {

    @Test
    void shouldCreateModuleProgress() {
        UserProfileId userId = UserProfileId.generate();
        ModuleName moduleName = new ModuleName("vocabulary");
        ModuleLevel level = new ModuleLevel("b1");

        ModuleProgress progress = ModuleProgress.create(userId, moduleName, level);

        assertNotNull(progress.id());
        assertEquals(userId, progress.userId());
        assertEquals(moduleName, progress.moduleName());
        assertEquals(level, progress.level());
        assertEquals(0, progress.currentUnit());
        assertTrue(progress.completedUnits().isEmpty());
        assertTrue(progress.scores().isEmpty());
    }

    @Test
    void shouldCompleteUnit() {
        ModuleProgress progress = ModuleProgressMother.create();

        ModuleProgress updated = progress.completeUnit(0, 85);

        assertEquals(1, updated.currentUnit());
        assertTrue(updated.completedUnits().contains(0));
        assertEquals(85, updated.scores().get(0));
    }

    @Test
    void shouldCalculateAverageScore() {
        ModuleProgress progress = ModuleProgressMother.create();

        ModuleProgress afterUnit0 = progress.completeUnit(0, 80);
        ModuleProgress afterUnit1 = afterUnit0.completeUnit(1, 90);

        assertEquals(85, afterUnit1.averageScore());
    }

    @Test
    void shouldReturnZeroAverageWhenNoScores() {
        ModuleProgress progress = ModuleProgressMother.create();

        assertEquals(0, progress.averageScore());
    }

    @Test
    void shouldDetectLevelComplete() {
        ModuleProgress progress = ModuleProgressMother.create();

        ModuleProgress afterUnit0 = progress.completeUnit(0, 80);
        ModuleProgress afterUnit1 = afterUnit0.completeUnit(1, 90);
        ModuleProgress afterUnit2 = afterUnit1.completeUnit(2, 75);

        assertTrue(afterUnit2.isLevelComplete(3));
        assertFalse(afterUnit2.isLevelComplete(5));
    }

    @Test
    void shouldNotDuplicateCompletedUnits() {
        ModuleProgress progress = ModuleProgressMother.create();

        ModuleProgress first = progress.completeUnit(0, 70);
        ModuleProgress second = first.completeUnit(0, 90);

        assertEquals(1, second.completedUnits().size());
        assertEquals(90, second.scores().get(0));
    }
}
