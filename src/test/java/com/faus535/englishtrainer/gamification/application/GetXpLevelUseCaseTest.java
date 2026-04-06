package com.faus535.englishtrainer.gamification.application;

import com.faus535.englishtrainer.gamification.domain.XpLevel;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.UserProfileMother;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import com.faus535.englishtrainer.user.infrastructure.InMemoryUserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GetXpLevelUseCaseTest {

    private InMemoryUserProfileRepository repository;
    private GetXpLevelUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserProfileRepository();
        useCase = new GetXpLevelUseCase(repository);
    }

    @Test
    void shouldReturnXpLevelForUser() throws Exception {
        UserProfile profile = UserProfileMother.withXp(250);
        repository.save(profile);

        XpLevel result = useCase.execute(profile.id());

        assertEquals(1, result.level());
        assertEquals("Elementary", result.name());
        assertEquals(250, result.currentXp());
    }

    @Test
    void shouldThrowUserProfileNotFoundExceptionWhenMissing() {
        UserProfileId randomId = UserProfileId.generate();
        assertThrows(UserProfileNotFoundException.class,
                () -> useCase.execute(randomId));
    }
}
