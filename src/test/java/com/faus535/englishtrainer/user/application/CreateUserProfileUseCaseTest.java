package com.faus535.englishtrainer.user.application;

import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.infrastructure.InMemoryUserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.*;

final class CreateUserProfileUseCaseTest {

    private InMemoryUserProfileRepository repository;
    private CreateUserProfileUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserProfileRepository();
        ApplicationEventPublisher publisher = event -> {};
        useCase = new CreateUserProfileUseCase(repository, publisher);
    }

    @Test
    void shouldCreateProfile() {
        UserProfile profile = useCase.execute();

        assertNotNull(profile);
        assertNotNull(profile.id());
        assertFalse(profile.testCompleted());
        assertEquals(0, profile.xp());
    }

    @Test
    void shouldPersistProfile() {
        UserProfile profile = useCase.execute();

        assertEquals(1, repository.count());
        assertTrue(repository.contains(profile.id()));
    }
}
