package com.faus535.englishtrainer.gamification.domain;

import java.util.List;
import java.util.Optional;

public interface AchievementRepository {

    List<Achievement> findAll();

    Optional<Achievement> findById(AchievementId id);
}
