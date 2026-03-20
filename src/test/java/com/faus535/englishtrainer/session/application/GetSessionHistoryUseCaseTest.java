package com.faus535.englishtrainer.session.application;

import com.faus535.englishtrainer.session.domain.Session;
import com.faus535.englishtrainer.session.domain.SessionMother;
import com.faus535.englishtrainer.session.infrastructure.InMemorySessionRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

final class GetSessionHistoryUseCaseTest {

    private InMemorySessionRepository sessionRepository;
    private GetSessionHistoryUseCase useCase;

    @BeforeEach
    void setUp() {
        sessionRepository = new InMemorySessionRepository();
        useCase = new GetSessionHistoryUseCase(sessionRepository);
    }

    @Test
    void shouldReturnSessionsForUser() {
        UserProfileId userId = UserProfileId.generate();
        sessionRepository.save(SessionMother.create(userId));
        sessionRepository.save(SessionMother.create(userId));

        List<Session> result = useCase.execute(userId);

        assertEquals(2, result.size());
    }
}
