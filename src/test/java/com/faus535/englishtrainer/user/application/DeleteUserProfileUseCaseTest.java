package com.faus535.englishtrainer.user.application;

import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import com.faus535.englishtrainer.user.infrastructure.InMemoryUserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class DeleteUserProfileUseCaseTest {

    private InMemoryUserProfileRepository repository;
    private DeleteUserProfileUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserProfileRepository();
        useCase = new DeleteUserProfileUseCase(repository);
    }

    @Test
    void shouldDeleteExistingProfile() throws UserProfileNotFoundException {
        UserProfile profile = UserProfile.create();
        repository.save(profile);

        useCase.execute(profile.id());

        assertFalse(repository.contains(profile.id()));
    }

    @Test
    void shouldThrowWhenProfileNotFound() {
        UserProfileId unknownId = UserProfileId.generate();

        assertThrows(UserProfileNotFoundException.class, () -> useCase.execute(unknownId));
    }
}
