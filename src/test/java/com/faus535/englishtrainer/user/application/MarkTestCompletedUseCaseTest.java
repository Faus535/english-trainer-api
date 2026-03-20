package com.faus535.englishtrainer.user.application;

import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import com.faus535.englishtrainer.user.infrastructure.InMemoryUserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class MarkTestCompletedUseCaseTest {

    private InMemoryUserProfileRepository repository;
    private MarkTestCompletedUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserProfileRepository();
        useCase = new MarkTestCompletedUseCase(repository);
    }

    @Test
    void shouldMarkTestAsCompleted() throws UserProfileNotFoundException {
        UserProfile profile = UserProfile.create();
        repository.save(profile);

        useCase.execute(profile.id());

        UserProfile updated = repository.findById(profile.id()).orElseThrow();
        assertTrue(updated.testCompleted());
    }
}
