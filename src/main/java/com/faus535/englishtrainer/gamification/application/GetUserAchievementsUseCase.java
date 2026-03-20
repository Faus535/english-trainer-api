package com.faus535.englishtrainer.gamification.application;

import com.faus535.englishtrainer.gamification.domain.UserAchievement;
import com.faus535.englishtrainer.gamification.domain.UserAchievementRepository;
import com.faus535.englishtrainer.shared.domain.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.util.List;

@UseCase
public final class GetUserAchievementsUseCase {

    private final UserAchievementRepository userAchievementRepository;

    public GetUserAchievementsUseCase(UserAchievementRepository userAchievementRepository) {
        this.userAchievementRepository = userAchievementRepository;
    }

    public List<UserAchievement> execute(UserProfileId userId) {
        return userAchievementRepository.findByUser(userId);
    }
}
