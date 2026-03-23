package com.faus535.englishtrainer.minigame.domain;

import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.util.List;

public interface MiniGameScoreRepository {

    MiniGameScore save(MiniGameScore score);

    List<MiniGameScore> findByUserIdAndGameType(UserProfileId userId, String gameType);
}
