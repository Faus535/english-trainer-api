package com.faus535.englishtrainer.session.infrastructure.controller;

import com.faus535.englishtrainer.session.domain.*;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

final class SessionResponseMapperTest {

    @Test
    void should_map_block_exercises_correctly() {
        Session session = Session.reconstitute(
                SessionId.generate(), UserProfileId.generate(),
                new SessionMode("short"), new SessionType("normal"),
                "listening", null, null,
                List.of(
                        new SessionBlock("warmup", "vocabulary", 5, 2, List.of()),
                        new SessionBlock("core", "listening", 10, 1, List.of())
                ),
                List.of(
                        new SessionExercise(0, 0, "flashcard", List.of(), 5, null),
                        new SessionExercise(1, 0, "multiple_choice", List.of(), 3, null),
                        new SessionExercise(2, 1, "listening", List.of(), 4, null)
                ),
                false, Instant.now(), null, null
        );

        SessionResponse response = SessionResponseMapper.toResponse(session);

        assertEquals(2, response.blocks().size());
        assertEquals(2, response.blocks().get(0).exercises().size());
        assertEquals(1, response.blocks().get(1).exercises().size());
        assertEquals("flashcard", response.blocks().get(0).exercises().get(0).exerciseType());
        assertEquals("listening", response.blocks().get(1).exercises().get(0).exerciseType());
    }

    @Test
    void should_handle_duplicate_block_types() {
        Session session = Session.reconstitute(
                SessionId.generate(), UserProfileId.generate(),
                new SessionMode("short"), new SessionType("normal"),
                "listening", null, null,
                List.of(
                        new SessionBlock("warmup", "vocabulary", 5, 1, List.of()),
                        new SessionBlock("warmup", "vocabulary", 5, 1, List.of())
                ),
                List.of(
                        new SessionExercise(0, 0, "flashcard", List.of(), 5,
                                new ExerciseResult(5, 5, 0, Instant.now())),
                        new SessionExercise(1, 1, "flashcard", List.of(), 3, null)
                ),
                false, Instant.now(), null, null
        );

        SessionResponse response = SessionResponseMapper.toResponse(session);

        assertTrue(response.blocks().get(0).blockCompleted());
        assertFalse(response.blocks().get(1).blockCompleted());
        assertEquals(1, response.blocks().get(0).completedExercises());
        assertEquals(0, response.blocks().get(1).completedExercises());
    }

    @Test
    void should_handle_session_with_no_exercises() {
        Session session = Session.reconstitute(
                SessionId.generate(), UserProfileId.generate(),
                new SessionMode("short"), new SessionType("normal"),
                "listening", null, null,
                List.of(
                        new SessionBlock("warmup", "vocabulary", 5, 0, List.of())
                ),
                List.of(),
                false, Instant.now(), null, null
        );

        SessionResponse response = SessionResponseMapper.toResponse(session);

        assertEquals(1, response.blocks().size());
        assertTrue(response.blocks().get(0).exercises().isEmpty());
        assertTrue(response.exercises().isEmpty());
    }
}
