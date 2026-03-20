package com.faus535.englishtrainer.gamification.application;

import com.faus535.englishtrainer.gamification.domain.Achievement;
import com.faus535.englishtrainer.gamification.domain.AchievementRepository;
import com.faus535.englishtrainer.shared.domain.annotation.UseCase;

import java.util.List;

@UseCase
public final class GetAllAchievementsUseCase {

    private final AchievementRepository achievementRepository;

    public GetAllAchievementsUseCase(AchievementRepository achievementRepository) {
        this.achievementRepository = achievementRepository;
    }

    public List<Achievement> execute() {
        return achievementRepository.findAll();
    }
}
