package com.faus535.englishtrainer.user.application;

import com.faus535.englishtrainer.user.domain.UserLevel;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.error.InvalidModuleException;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import com.faus535.englishtrainer.user.infrastructure.InMemoryUserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class UpdateModuleLevelUseCaseTest {

    private InMemoryUserProfileRepository repository;
    private UpdateModuleLevelUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserProfileRepository();
        useCase = new UpdateModuleLevelUseCase(repository);
    }

    @Test
    void shouldUpdateModuleLevel() throws UserProfileNotFoundException, InvalidModuleException {
        UserProfile profile = UserProfile.create();
        repository.save(profile);

        useCase.execute(profile.id(), "vocabulary", new UserLevel("b2"));

        UserProfile updated = repository.findById(profile.id()).orElseThrow();
        assertEquals(new UserLevel("b2"), updated.levelVocabulary());
    }
}
