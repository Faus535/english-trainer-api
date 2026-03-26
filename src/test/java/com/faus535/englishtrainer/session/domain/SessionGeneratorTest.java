package com.faus535.englishtrainer.session.domain;

import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

final class SessionGeneratorTest {

    @Test
    void shouldGenerateNormalSession() {
        UserProfileId userId = UserProfileId.generate();
        SessionMode mode = new SessionMode("full");

        Session session = SessionGenerator.generateNormal(userId, mode, 0);

        assertEquals("normal", session.sessionType().value());
        assertEquals("full", session.mode().value());
        assertEquals(userId, session.userId());
        assertEquals(4, session.blocks().size());
        assertEquals("warmup", session.blocks().get(0).blockType());
        assertEquals("listening", session.blocks().get(1).blockType());
        assertEquals("secondary", session.blocks().get(2).blockType());
        assertEquals("practice", session.blocks().get(3).blockType());
        assertEquals("vocabulary", session.secondaryModule());
    }

    @Test
    void shouldGenerateIntegratorSession() {
        UserProfileId userId = UserProfileId.generate();
        SessionMode mode = new SessionMode("full");

        Session session = SessionGenerator.generateIntegrator(userId, mode, "travel");

        assertEquals("integrator", session.sessionType().value());
        assertEquals("travel", session.integratorTheme());
        assertEquals(userId, session.userId());
        assertEquals(2, session.blocks().size());
        assertEquals("warmup", session.blocks().get(0).blockType());
        assertEquals("integrator", session.blocks().get(1).blockType());
    }

    @Test
    void shouldDetectIntegratorTiming() {
        assertFalse(SessionGenerator.shouldBeIntegrator(0));
        assertFalse(SessionGenerator.shouldBeIntegrator(1));
        assertFalse(SessionGenerator.shouldBeIntegrator(4));
        assertTrue(SessionGenerator.shouldBeIntegrator(5));
        assertTrue(SessionGenerator.shouldBeIntegrator(10));
        assertTrue(SessionGenerator.shouldBeIntegrator(15));
        assertFalse(SessionGenerator.shouldBeIntegrator(7));
    }

    @Test
    void shouldAssignCorrectBlockIndexToExercises() {
        List<SessionBlock> blocks = List.of(
                new SessionBlock("warmup", "review", 3, 3, List.of()),
                new SessionBlock("listening", "listening", 7, 4, List.of()),
                new SessionBlock("secondary", "vocabulary", 7, 3, List.of())
        );

        List<SessionExercise> exercises = SessionGenerator.buildExercises(blocks);

        assertEquals(10, exercises.size());
        // First 3 exercises -> block 0
        for (int i = 0; i < 3; i++) {
            assertEquals(0, exercises.get(i).blockIndex());
        }
        // Next 4 exercises -> block 1
        for (int i = 3; i < 7; i++) {
            assertEquals(1, exercises.get(i).blockIndex());
        }
        // Last 3 exercises -> block 2
        for (int i = 7; i < 10; i++) {
            assertEquals(2, exercises.get(i).blockIndex());
        }
    }

    @Test
    void shouldAssignSequentialExerciseIndexes() {
        List<SessionBlock> blocks = List.of(
                new SessionBlock("warmup", "review", 3, 2, List.of()),
                new SessionBlock("listening", "listening", 7, 3, List.of())
        );

        List<SessionExercise> exercises = SessionGenerator.buildExercises(blocks);

        for (int i = 0; i < exercises.size(); i++) {
            assertEquals(i, exercises.get(i).exerciseIndex());
        }
    }
}
