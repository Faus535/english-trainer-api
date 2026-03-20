package com.faus535.englishtrainer.gamification.infrastructure;

import com.faus535.englishtrainer.gamification.domain.Achievement;
import com.faus535.englishtrainer.gamification.domain.AchievementId;
import com.faus535.englishtrainer.gamification.domain.AchievementRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class InMemoryAchievementRepository implements AchievementRepository {

    private final Map<AchievementId, Achievement> store = new HashMap<>();

    @Override
    public List<Achievement> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<Achievement> findById(AchievementId id) {
        return Optional.ofNullable(store.get(id));
    }

    public void save(Achievement achievement) {
        store.put(achievement.id(), achievement);
    }
}
