package com.faus535.englishtrainer.gamification.application;

import com.faus535.englishtrainer.gamification.domain.Achievement;
import com.faus535.englishtrainer.gamification.domain.AchievementId;
import com.faus535.englishtrainer.gamification.domain.AchievementRepository;
import com.faus535.englishtrainer.gamification.domain.UserAchievement;
import com.faus535.englishtrainer.gamification.domain.UserAchievementRepository;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.UserProfileRepository;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@UseCase
public class CheckAndUnlockAchievementsUseCase {

    private final UserProfileRepository userProfileRepository;
    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CheckAndUnlockAchievementsUseCase(UserProfileRepository userProfileRepository,
                                              AchievementRepository achievementRepository,
                                              UserAchievementRepository userAchievementRepository,
                                              ApplicationEventPublisher eventPublisher) {
        this.userProfileRepository = userProfileRepository;
        this.achievementRepository = achievementRepository;
        this.userAchievementRepository = userAchievementRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public List<Achievement> execute(UserProfileId userId) throws UserProfileNotFoundException {
        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new UserProfileNotFoundException(userId));

        List<Achievement> newlyUnlocked = new ArrayList<>();

        checkAndUnlock(userId, new AchievementId("first_session"), profile.sessionCount() >= 1, newlyUnlocked);
        checkAndUnlock(userId, new AchievementId("sessions_10"), profile.sessionCount() >= 10, newlyUnlocked);
        checkAndUnlock(userId, new AchievementId("sessions_25"), profile.sessionCount() >= 25, newlyUnlocked);
        checkAndUnlock(userId, new AchievementId("sessions_50"), profile.sessionCount() >= 50, newlyUnlocked);
        checkAndUnlock(userId, new AchievementId("sessions_100"), profile.sessionCount() >= 100, newlyUnlocked);

        checkAndUnlock(userId, new AchievementId("first_test"), profile.testCompleted(), newlyUnlocked);

        checkAndUnlock(userId, new AchievementId("xp_1000"), profile.xp() >= 1000, newlyUnlocked);
        checkAndUnlock(userId, new AchievementId("xp_5000"), profile.xp() >= 5000, newlyUnlocked);

        return newlyUnlocked;
    }

    private void checkAndUnlock(UserProfileId userId, AchievementId achievementId,
                                 boolean condition, List<Achievement> newlyUnlocked) {
        if (!condition) {
            return;
        }
        if (userAchievementRepository.existsByUserAndAchievement(userId, achievementId)) {
            return;
        }
        achievementRepository.findById(achievementId).ifPresent(achievement -> {
            UserAchievement userAchievement = UserAchievement.create(userId, achievementId);
            userAchievementRepository.save(userAchievement);
            userAchievement.pullDomainEvents().forEach(eventPublisher::publishEvent);
            newlyUnlocked.add(achievement);
        });
    }
}
