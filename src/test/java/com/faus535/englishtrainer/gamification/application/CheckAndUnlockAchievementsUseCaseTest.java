package com.faus535.englishtrainer.gamification.application;

import com.faus535.englishtrainer.gamification.domain.Achievement;
import com.faus535.englishtrainer.gamification.domain.AchievementId;
import com.faus535.englishtrainer.gamification.domain.AchievementMother;
import com.faus535.englishtrainer.gamification.infrastructure.InMemoryAchievementRepository;
import com.faus535.englishtrainer.gamification.infrastructure.InMemoryUserAchievementRepository;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import com.faus535.englishtrainer.user.infrastructure.InMemoryUserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

final class CheckAndUnlockAchievementsUseCaseTest {

    private InMemoryUserProfileRepository userProfileRepository;
    private InMemoryAchievementRepository achievementRepository;
    private InMemoryUserAchievementRepository userAchievementRepository;
    private CheckAndUnlockAchievementsUseCase useCase;

    @BeforeEach
    void setUp() {
        userProfileRepository = new InMemoryUserProfileRepository();
        achievementRepository = new InMemoryAchievementRepository();
        userAchievementRepository = new InMemoryUserAchievementRepository();
        ApplicationEventPublisher publisher = event -> {};
        useCase = new CheckAndUnlockAchievementsUseCase(userProfileRepository, achievementRepository,
                userAchievementRepository, publisher);
    }

    @Test
    void shouldUnlockAchievementWhenConditionMet() throws UserProfileNotFoundException {
        UserProfile profile = UserProfile.create().recordSession();
        userProfileRepository.save(profile);

        Achievement firstSession = Achievement.reconstitute(
                new AchievementId("first_session"), "First Session", "Complete your first session", "star", 50);
        achievementRepository.save(firstSession);

        List<Achievement> unlocked = useCase.execute(profile.id());

        assertEquals(1, unlocked.size());
        assertEquals("first_session", unlocked.get(0).id().value());
    }

    @Test
    void shouldNotUnlockAlreadyUnlockedAchievement() throws UserProfileNotFoundException {
        UserProfile profile = UserProfile.create().recordSession();
        userProfileRepository.save(profile);

        Achievement firstSession = Achievement.reconstitute(
                new AchievementId("first_session"), "First Session", "Complete your first session", "star", 50);
        achievementRepository.save(firstSession);

        useCase.execute(profile.id());
        List<Achievement> secondRun = useCase.execute(profile.id());

        assertTrue(secondRun.isEmpty());
    }
}
