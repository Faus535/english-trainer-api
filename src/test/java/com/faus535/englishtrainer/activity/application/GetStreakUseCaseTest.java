package com.faus535.englishtrainer.activity.application;

import com.faus535.englishtrainer.activity.domain.ActivityDateMother;
import com.faus535.englishtrainer.activity.domain.StreakInfo;
import com.faus535.englishtrainer.activity.infrastructure.InMemoryActivityDateRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class GetStreakUseCaseTest {

    private InMemoryActivityDateRepository repository;
    private GetStreakUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryActivityDateRepository();
        useCase = new GetStreakUseCase(repository);
    }

    @Test
    void shouldCalculateCurrentStreak() {
        UserProfileId userId = UserProfileId.generate();
        LocalDate today = LocalDate.now();
        repository.save(ActivityDateMother.create(userId, today));
        repository.save(ActivityDateMother.create(userId, today.minusDays(1)));
        repository.save(ActivityDateMother.create(userId, today.minusDays(2)));

        StreakInfo result = useCase.execute(userId);

        assertEquals(3, result.currentStreak());
        assertTrue(result.bestStreak() >= 3);
        assertEquals(today, result.lastActiveDate());
    }

    @Test
    void shouldReturnZeroStreakForNoActivity() {
        UserProfileId unknownUser = UserProfileId.generate();

        StreakInfo result = useCase.execute(unknownUser);

        assertEquals(0, result.currentStreak());
        assertEquals(0, result.bestStreak());
        assertNull(result.lastActiveDate());
    }
}
