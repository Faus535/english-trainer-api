package com.faus535.englishtrainer.gamification.application;

import com.faus535.englishtrainer.gamification.domain.Achievement;
import com.faus535.englishtrainer.gamification.domain.AchievementMother;
import com.faus535.englishtrainer.gamification.infrastructure.InMemoryAchievementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GetAllAchievementsUseCaseTest {

    private InMemoryAchievementRepository repository;
    private GetAllAchievementsUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryAchievementRepository();
        useCase = new GetAllAchievementsUseCase(repository);
    }

    @Test
    void shouldReturnAllAchievements() {
        Achievement a1 = AchievementMother.create();
        Achievement a2 = AchievementMother.withId("streak-7");
        repository.save(a1);
        repository.save(a2);

        List<Achievement> result = useCase.execute();

        assertEquals(2, result.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoAchievements() {
        List<Achievement> result = useCase.execute();

        assertTrue(result.isEmpty());
    }
}
