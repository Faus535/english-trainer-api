package com.faus535.englishtrainer.gamification.application;

import com.faus535.englishtrainer.gamification.domain.Achievement;
import com.faus535.englishtrainer.gamification.domain.AchievementRepository;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
public class GetAllAchievementsUseCase {

    private final AchievementRepository achievementRepository;

    GetAllAchievementsUseCase(AchievementRepository achievementRepository) {
        this.achievementRepository = achievementRepository;
    }

    @Transactional(readOnly = true)
    public List<Achievement> execute() {
        return achievementRepository.findAll();
    }
}
