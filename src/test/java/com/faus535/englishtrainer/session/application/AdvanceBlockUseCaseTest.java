package com.faus535.englishtrainer.session.application;

import com.faus535.englishtrainer.session.domain.*;
import com.faus535.englishtrainer.session.domain.error.BlockNotCompletedException;
import com.faus535.englishtrainer.session.domain.error.SessionNotFoundException;
import com.faus535.englishtrainer.session.infrastructure.InMemorySessionRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class AdvanceBlockUseCaseTest {

    private InMemorySessionRepository sessionRepository;
    private AdvanceBlockUseCase useCase;

    @BeforeEach
    void setUp() {
        sessionRepository = new InMemorySessionRepository();
        useCase = new AdvanceBlockUseCase(sessionRepository);
    }

    @Test
    void shouldAdvanceBlock_whenAllExercisesCompleted()
            throws SessionNotFoundException, BlockNotCompletedException {
        Session session = SessionMother.createWithCompletedBlock(0);
        sessionRepository.save(session);

        AdvanceBlockUseCase.AdvanceBlockResult result =
                useCase.execute(session.userId(), session.id(), 0);

        assertEquals(0, result.blockIndex());
        assertTrue(result.blockCompleted());
        assertEquals(1, result.nextBlockIndex());
        assertFalse(result.sessionCompleted());
    }

    @Test
    void shouldRejectAdvance_whenBlockIncomplete() {
        Session session = SessionMother.createWithCompletedBlock(1); // block 0 is NOT completed
        sessionRepository.save(session);

        assertThrows(BlockNotCompletedException.class,
                () -> useCase.execute(session.userId(), session.id(), 0));
    }

    @Test
    void shouldThrow_whenSessionNotFound() {
        assertThrows(SessionNotFoundException.class,
                () -> useCase.execute(UserProfileId.generate(), SessionId.generate(), 0));
    }
}
