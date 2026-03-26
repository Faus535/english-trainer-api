package com.faus535.englishtrainer.session.application;

import com.faus535.englishtrainer.session.domain.Session;
import com.faus535.englishtrainer.session.domain.SessionId;
import com.faus535.englishtrainer.session.domain.SessionMother;
import com.faus535.englishtrainer.session.domain.error.IncompleteSessionException;
import com.faus535.englishtrainer.session.domain.error.SessionNotFoundException;
import com.faus535.englishtrainer.session.infrastructure.InMemorySessionRepository;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import com.faus535.englishtrainer.user.infrastructure.InMemoryUserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.*;

final class CompleteSessionUseCaseTest {

    private InMemorySessionRepository sessionRepository;
    private InMemoryUserProfileRepository userProfileRepository;
    private CompleteSessionUseCase useCase;

    @BeforeEach
    void setUp() {
        sessionRepository = new InMemorySessionRepository();
        userProfileRepository = new InMemoryUserProfileRepository();
        ApplicationEventPublisher publisher = event -> {};
        useCase = new CompleteSessionUseCase(sessionRepository, userProfileRepository, publisher);
    }

    @Test
    void shouldCompleteSessionAndUpdateUserProfile() throws SessionNotFoundException, UserProfileNotFoundException, IncompleteSessionException {
        UserProfile profile = UserProfile.create();
        userProfileRepository.save(profile);
        Session session = SessionMother.create(profile.id());
        sessionRepository.save(session);

        Session completed = useCase.execute(session.id(), 15);

        assertTrue(completed.completed());
        assertEquals(15, completed.durationMinutes());

        UserProfile updatedProfile = userProfileRepository.findById(profile.id()).orElseThrow();
        assertEquals(1, updatedProfile.sessionCount());
    }

    @Test
    void shouldThrowWhenSessionNotFound() {
        SessionId unknownId = SessionId.generate();

        assertThrows(SessionNotFoundException.class, () -> useCase.execute(unknownId, 10));
    }
}
