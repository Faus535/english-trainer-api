package com.faus535.englishtrainer.gamification.application;

import com.faus535.englishtrainer.gamification.domain.UserAchievement;
import com.faus535.englishtrainer.gamification.domain.UserAchievementRepository;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
public class GetUserAchievementsUseCase {

    private final UserAchievementRepository userAchievementRepository;

    public GetUserAchievementsUseCase(UserAchievementRepository userAchievementRepository) {
        this.userAchievementRepository = userAchievementRepository;
    }

    @Transactional(readOnly = true)
    public List<UserAchievement> execute(UserProfileId userId) {
        return userAchievementRepository.findByUser(userId);
    }
}
