package com.faus535.englishtrainer.user.application;

import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import com.faus535.englishtrainer.user.domain.vo.StudyModule;
import com.faus535.englishtrainer.user.infrastructure.InMemoryStudySessionRepository;
import com.faus535.englishtrainer.user.infrastructure.InMemoryUserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class RecordStudySessionUseCaseTest {

    private InMemoryUserProfileRepository userProfileRepository;
    private InMemoryStudySessionRepository studySessionRepository;
    private RecordStudySessionUseCase useCase;

    @BeforeEach
    void setUp() {
        userProfileRepository = new InMemoryUserProfileRepository();
        studySessionRepository = new InMemoryStudySessionRepository();
        useCase = new RecordStudySessionUseCase(userProfileRepository, studySessionRepository);
    }

    @Test
    void execute_savesSession_whenUserExists() throws UserProfileNotFoundException {
        UserProfile profile = UserProfile.create();
        userProfileRepository.save(profile);

        useCase.execute(profile.id().value(), StudyModule.ARTICLE, 600);

        assertEquals(1, studySessionRepository.count());
    }

    @Test
    void execute_throwsUserNotFoundException_whenUserNotFound() {
        UserProfileId unknownId = UserProfileId.generate();

        assertThrows(UserProfileNotFoundException.class,
                () -> useCase.execute(unknownId.value(), StudyModule.TALK, 300));
    }
}
