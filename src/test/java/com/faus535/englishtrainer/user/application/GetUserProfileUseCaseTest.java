package com.faus535.englishtrainer.user.application;

import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.UserProfileMother;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import com.faus535.englishtrainer.user.infrastructure.InMemoryUserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GetUserProfileUseCaseTest {

    private InMemoryUserProfileRepository repository;
    private GetUserProfileUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserProfileRepository();
        useCase = new GetUserProfileUseCase(repository);
    }

    @Test
    void shouldReturnProfileWhenFound() throws Exception {
        UserProfile profile = UserProfileMother.create();
        repository.save(profile);

        UserProfile result = useCase.execute(profile.id());

        assertEquals(profile.id(), result.id());
        assertEquals(profile.xp(), result.xp());
    }

    @Test
    void shouldThrowUserProfileNotFoundExceptionWhenMissing() {
        UserProfileId randomId = UserProfileId.generate();
        assertThrows(UserProfileNotFoundException.class,
                () -> useCase.execute(randomId));
    }
}
