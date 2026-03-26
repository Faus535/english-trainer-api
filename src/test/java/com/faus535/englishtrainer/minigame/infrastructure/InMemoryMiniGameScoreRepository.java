package com.faus535.englishtrainer.minigame.infrastructure;

import com.faus535.englishtrainer.minigame.domain.MiniGameScore;
import com.faus535.englishtrainer.minigame.domain.MiniGameScoreRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.util.*;

public final class InMemoryMiniGameScoreRepository implements MiniGameScoreRepository {

    private final Map<UUID, MiniGameScore> store = new HashMap<>();

    @Override
    public MiniGameScore save(MiniGameScore score) {
        store.put(score.id().value(), score);
        return score;
    }

    @Override
    public List<MiniGameScore> findByUserIdAndGameType(UserProfileId userId, String gameType) {
        return store.values().stream()
                .filter(s -> s.userId().equals(userId) && s.gameType().equals(gameType))
                .toList();
    }

    public int count() {
        return store.size();
    }
}
