package com.faus535.englishtrainer.session.application;

import com.faus535.englishtrainer.session.domain.*;
import com.faus535.englishtrainer.session.domain.error.SessionNotFoundException;
import com.faus535.englishtrainer.session.infrastructure.InMemorySessionRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

final class GetBlockExercisesUseCaseTest {

    private InMemorySessionRepository sessionRepository;
    private GetBlockExercisesUseCase useCase;

    @BeforeEach
    void setUp() {
        sessionRepository = new InMemorySessionRepository();
        useCase = new GetBlockExercisesUseCase(sessionRepository);
    }

    @Test
    void shouldReturnExercisesForBlock() throws SessionNotFoundException {
        Session session = SessionMother.createWithCompletedBlock(0);
        sessionRepository.save(session);

        List<SessionExercise> exercises =
                useCase.execute(session.userId(), session.id(), 0);

        assertFalse(exercises.isEmpty());
        assertTrue(exercises.stream().allMatch(ex -> ex.blockIndex() == 0));
    }

    @Test
    void shouldReturnEmptyList_forBlockWithNoExercises() throws SessionNotFoundException {
        Session session = SessionMother.createWithCompletedBlock(0);
        sessionRepository.save(session);

        List<SessionExercise> exercises =
                useCase.execute(session.userId(), session.id(), 99);

        assertTrue(exercises.isEmpty());
    }

    @Test
    void shouldThrow_whenSessionNotFound() {
        assertThrows(SessionNotFoundException.class,
                () -> useCase.execute(UserProfileId.generate(), SessionId.generate(), 0));
    }
}
