package com.faus535.englishtrainer.gamification.application;

import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.error.InvalidXpAmountException;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import com.faus535.englishtrainer.user.infrastructure.InMemoryUserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class GrantXpUseCaseTest {

    private InMemoryUserProfileRepository repository;
    private GrantXpUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserProfileRepository();
        useCase = new GrantXpUseCase(repository);
    }

    @Test
    void shouldAddXpToUserProfile() throws UserProfileNotFoundException, InvalidXpAmountException {
        UserProfile profile = UserProfile.create();
        repository.save(profile);

        GrantXpUseCase.XpGrantResult result = useCase.execute(profile.id(), 100, "session_complete");

        assertEquals(100, result.xpGranted());
        assertEquals(100, result.totalXp());

        UserProfile updated = repository.findById(profile.id()).orElseThrow();
        assertEquals(100, updated.xp());
    }

    @Test
    void shouldThrowWhenProfileNotFound() {
        UserProfileId unknownId = UserProfileId.generate();

        assertThrows(UserProfileNotFoundException.class,
                () -> useCase.execute(unknownId, 50, "test"));
    }
}
