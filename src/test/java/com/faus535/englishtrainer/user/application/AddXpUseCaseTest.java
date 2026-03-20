package com.faus535.englishtrainer.user.application;

import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.error.InvalidXpAmountException;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import com.faus535.englishtrainer.user.infrastructure.InMemoryUserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.*;

final class AddXpUseCaseTest {

    private InMemoryUserProfileRepository repository;
    private AddXpUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserProfileRepository();
        ApplicationEventPublisher publisher = event -> {};
        useCase = new AddXpUseCase(repository, publisher);
    }

    @Test
    void shouldAddXpToProfile() throws UserProfileNotFoundException, InvalidXpAmountException {
        UserProfile profile = UserProfile.create();
        repository.save(profile);

        UserProfile updated = useCase.execute(profile.id(), 50);

        assertEquals(50, updated.xp());
    }

    @Test
    void shouldThrowWhenProfileNotFound() {
        UserProfileId unknownId = UserProfileId.generate();

        assertThrows(UserProfileNotFoundException.class, () -> useCase.execute(unknownId, 50));
    }
}
