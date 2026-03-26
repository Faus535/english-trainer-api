package com.faus535.englishtrainer.session.domain;

import com.faus535.englishtrainer.session.domain.error.IncompleteSessionException;
import com.faus535.englishtrainer.session.domain.event.SessionCompletedEvent;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.junit.jupiter.api.Test;

import java.util.List;

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
    void shouldCompleteSession() throws IncompleteSessionException {
        Session session = SessionMother.create();

        Session completed = session.complete(21);

        assertTrue(completed.completed());
        assertNotNull(completed.completedAt());
        assertEquals(21, completed.durationMinutes());
    }

    @Test
    void shouldRegisterCompletedEvent() throws IncompleteSessionException {
        Session session = SessionMother.create();

        Session completed = session.complete(21);

        var events = completed.pullDomainEvents();
        assertEquals(1, events.size());
        assertInstanceOf(SessionCompletedEvent.class, events.get(0));

        SessionCompletedEvent event = (SessionCompletedEvent) events.get(0);
        assertEquals(session.id(), event.sessionId());
        assertEquals(session.userId(), event.userId());
    }

    @Test
    void shouldReturnBlockCompleted_whenAllExercisesInBlockHaveResults() {
        Session session = SessionMother.createWithCompletedBlock(0);
        assertTrue(session.isBlockCompleted(0));
        assertFalse(session.isBlockCompleted(1));
    }

    @Test
    void shouldReturnBlockCompleted_forBlockWithNoExercises() {
        // Block 2 has no exercises -> auto-completed
        Session session = SessionMother.createWithCompletedBlock(0);
        assertTrue(session.isBlockCompleted(2));
    }

    @Test
    void shouldReturnAllBlocksCompleted_whenEveryExerciseHasResult() {
        Session session = SessionMother.createFullyCompleted();
        assertTrue(session.areAllBlocksCompleted());
    }

    @Test
    void shouldNotReturnAllBlocksCompleted_whenSomeExercisesLackResults() {
        Session session = SessionMother.createWithCompletedBlock(0);
        assertFalse(session.areAllBlocksCompleted());
    }

    @Test
    void shouldReturnBlockProgress() {
        Session session = SessionMother.createWithCompletedBlock(0);
        BlockProgress progress = session.getBlockProgress(0);
        assertEquals(0, progress.blockIndex());
        assertEquals(2, progress.totalExercises());
        assertEquals(2, progress.completedExercises());
        assertTrue(progress.isCompleted());
    }

    @Test
    void shouldReturnExercisesForBlock() {
        Session session = SessionMother.createWithCompletedBlock(0);
        List<SessionExercise> block0 = session.getExercisesForBlock(0);
        assertEquals(2, block0.size());
        assertTrue(block0.stream().allMatch(ex -> ex.blockIndex() == 0));
    }

    @Test
    void shouldThrowIncompleteSessionException_whenCompletingWithPendingExercises() {
        Session session = SessionMother.createWithCompletedBlock(0);
        assertThrows(IncompleteSessionException.class, () -> session.complete(15));
    }

    @Test
    void shouldCompleteSession_whenAllExercisesCompleted() throws IncompleteSessionException {
        Session session = SessionMother.createFullyCompleted();
        Session completed = session.complete(15);
        assertTrue(completed.completed());
    }

    @Test
    void shouldRejectCorrectCountExceedingTotalCount() {
        assertThrows(IllegalArgumentException.class, () ->
                new ExerciseResult(10, 5, 1000L, java.time.Instant.now()));
    }

    @Test
    void shouldValidateCorrectCountInRecordExerciseResult() {
        Session session = SessionMother.createWithCompletedBlock(0);
        ExerciseResult badResult = new ExerciseResult(3, 5, 1000L, java.time.Instant.now());
        // This should work (3 <= 5)
        Session updated = session.recordExerciseResult(2, badResult);
        assertNotNull(updated);
    }
}
