package com.faus535.englishtrainer.user.application;

import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import com.faus535.englishtrainer.user.domain.vo.EnglishLevel;
import com.faus535.englishtrainer.user.infrastructure.InMemoryUserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class UpdateEnglishLevelUseCaseTest {

    private InMemoryUserProfileRepository repository;
    private UpdateEnglishLevelUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserProfileRepository();
        useCase = new UpdateEnglishLevelUseCase(repository);
    }

    @Test
    void execute_updatesLevel_whenUserExists() throws UserProfileNotFoundException {
        UserProfile profile = UserProfile.create();
        repository.save(profile);

        useCase.execute(profile.id(), EnglishLevel.B2);

        UserProfile saved = repository.findById(profile.id()).orElseThrow();
        assertEquals(EnglishLevel.B2, saved.englishLevel());
    }

    @Test
    void execute_throwsUserNotFoundException_whenUserNotFound() {
        UserProfileId unknownId = UserProfileId.generate();

        assertThrows(UserProfileNotFoundException.class,
                () -> useCase.execute(unknownId, EnglishLevel.A1));
    }
}
