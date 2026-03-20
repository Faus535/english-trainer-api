package com.faus535.englishtrainer.assessment.domain;

import com.faus535.englishtrainer.assessment.domain.event.LevelTestCompletedEvent;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LevelTestResultTest {

    @Test
    void shouldCreateLevelTestResult() {
        UserProfileId userId = UserProfileId.generate();
        Map<String, String> assignedLevels = Map.of("vocabulary", "b2", "grammar", "b1");

        LevelTestResult result = LevelTestResult.create(userId, 75, 60, 50, 80, assignedLevels);

        assertNotNull(result.id());
        assertEquals(userId, result.userId());
        assertEquals(75, result.vocabularyScore());
        assertEquals(60, result.grammarScore());
        assertEquals(50, result.listeningScore());
        assertEquals(80, result.pronunciationScore());
        assertEquals(assignedLevels, result.assignedLevels());
        assertNotNull(result.completedAt());
    }

    @Test
    void shouldRegisterLevelTestCompletedEvent() {
        LevelTestResult result = LevelTestResultMother.create();

        var events = result.pullDomainEvents();

        assertFalse(events.isEmpty());
        assertEquals(1, events.size());
        assertTrue(events.getFirst() instanceof LevelTestCompletedEvent);
        LevelTestCompletedEvent event = (LevelTestCompletedEvent) events.getFirst();
        assertEquals(result.id(), event.resultId());
        assertEquals(result.userId(), event.userId());
    }
}
