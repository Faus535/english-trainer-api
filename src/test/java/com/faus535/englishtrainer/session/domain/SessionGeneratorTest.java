package com.faus535.englishtrainer.session.domain;

import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.junit.jupiter.api.Test;

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
}
