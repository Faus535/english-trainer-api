package com.faus535.englishtrainer.gamification.application;

import com.faus535.englishtrainer.gamification.domain.AchievementId;
import com.faus535.englishtrainer.gamification.domain.UserAchievement;
import com.faus535.englishtrainer.gamification.domain.UserAchievementMother;
import com.faus535.englishtrainer.gamification.infrastructure.InMemoryUserAchievementRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GetUserAchievementsUseCaseTest {

    private InMemoryUserAchievementRepository repository;
    private GetUserAchievementsUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserAchievementRepository();
        useCase = new GetUserAchievementsUseCase(repository);
    }

    @Test
    void shouldReturnUserAchievements() {
        UserProfileId userId = UserProfileId.generate();
        UserAchievement ua1 = UserAchievementMother.withUserAndAchievement(userId, new AchievementId("first-lesson"));
        UserAchievement ua2 = UserAchievementMother.withUserAndAchievement(userId, new AchievementId("streak-7"));
        repository.save(ua1);
        repository.save(ua2);

        List<UserAchievement> result = useCase.execute(userId);

        assertEquals(2, result.size());
    }

    @Test
    void shouldReturnEmptyListForUserWithNoAchievements() {
        UserProfileId unknownUser = UserProfileId.generate();

        List<UserAchievement> result = useCase.execute(unknownUser);

        assertTrue(result.isEmpty());
    }
}
