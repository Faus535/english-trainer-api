package com.faus535.englishtrainer.session.domain;

import com.faus535.englishtrainer.session.domain.event.SessionCompletedEvent;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class SessionTest {

    @Test
    void shouldCreateSession() {
        UserProfileId userId = UserProfileId.generate();

        Session session = SessionMother.create(userId);

        assertNotNull(session.id());
        assertEquals(userId, session.userId());
        assertEquals("full", session.mode().value());
        assertEquals("normal", session.sessionType().value());
        assertFalse(session.completed());
        assertNotNull(session.startedAt());
        assertNull(session.completedAt());
        assertNull(session.durationMinutes());
        assertEquals(4, session.blocks().size());
    }

    @Test
    void shouldCompleteSession() {
        Session session = SessionMother.create();

        Session completed = session.complete(21);

        assertTrue(completed.completed());
        assertNotNull(completed.completedAt());
        assertEquals(21, completed.durationMinutes());
    }

    @Test
    void shouldRegisterCompletedEvent() {
        Session session = SessionMother.create();

        Session completed = session.complete(21);

        var events = completed.pullDomainEvents();
        assertEquals(1, events.size());
        assertInstanceOf(SessionCompletedEvent.class, events.get(0));

        SessionCompletedEvent event = (SessionCompletedEvent) events.get(0);
        assertEquals(session.id(), event.sessionId());
        assertEquals(session.userId(), event.userId());
    }
}
