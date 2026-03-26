package com.faus535.englishtrainer.session.application;

import com.faus535.englishtrainer.learningpath.application.GetNextContentUseCase;
import com.faus535.englishtrainer.learningpath.infrastructure.InMemoryLearningPathRepository;
import com.faus535.englishtrainer.learningpath.infrastructure.InMemoryLearningUnitRepository;
import com.faus535.englishtrainer.session.domain.Session;
import com.faus535.englishtrainer.session.domain.SessionMode;
import com.faus535.englishtrainer.session.domain.SessionMother;
import com.faus535.englishtrainer.session.domain.error.ActiveSessionExistsException;
import com.faus535.englishtrainer.session.infrastructure.InMemorySessionRepository;
import com.faus535.englishtrainer.spacedrepetition.infrastructure.InMemorySpacedRepetitionRepository;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import com.faus535.englishtrainer.user.infrastructure.InMemoryUserProfileRepository;
import com.faus535.englishtrainer.vocabulary.infrastructure.InMemoryVocabRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class GenerateSessionUseCaseTest {

    private InMemoryUserProfileRepository userProfileRepository;
    private InMemorySessionRepository sessionRepository;
    private GenerateSessionUseCase useCase;

    @BeforeEach
    void setUp() {
        userProfileRepository = new InMemoryUserProfileRepository();
        sessionRepository = new InMemorySessionRepository();
        GetNextContentUseCase getNextContentUseCase = new GetNextContentUseCase(
                new InMemoryLearningPathRepository(),
                new InMemoryLearningUnitRepository(),
                new InMemorySpacedRepetitionRepository(),
                new InMemoryVocabRepository()
        );
        useCase = new GenerateSessionUseCase(userProfileRepository, sessionRepository, getNextContentUseCase);
    }

    @Test
    void shouldGenerateNewSession() throws UserProfileNotFoundException, ActiveSessionExistsException {
        UserProfile profile = UserProfile.create();
        userProfileRepository.save(profile);

        Session session = useCase.execute(profile.id(), new SessionMode("full"));

        assertNotNull(session);
        assertNotNull(session.id());
        assertEquals(profile.id(), session.userId());
        assertFalse(session.completed());
    }

    @Test
    void shouldThrowWhenActiveSessionExists() throws UserProfileNotFoundException, ActiveSessionExistsException {
        UserProfile profile = UserProfile.create();
        userProfileRepository.save(profile);

        useCase.execute(profile.id(), new SessionMode("full"));

        assertThrows(ActiveSessionExistsException.class,
                () -> useCase.execute(profile.id(), new SessionMode("full")));
    }
}
