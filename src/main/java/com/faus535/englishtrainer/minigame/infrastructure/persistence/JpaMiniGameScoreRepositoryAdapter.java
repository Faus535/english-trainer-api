package com.faus535.englishtrainer.minigame.infrastructure.persistence;

import com.faus535.englishtrainer.minigame.domain.MiniGameScore;
import com.faus535.englishtrainer.minigame.domain.MiniGameScoreRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
class JpaMiniGameScoreRepositoryAdapter implements MiniGameScoreRepository {

    private final JpaMiniGameScoreRepository jpaRepository;

    JpaMiniGameScoreRepositoryAdapter(JpaMiniGameScoreRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public MiniGameScore save(MiniGameScore score) {
        MiniGameScoreEntity entity = MiniGameScoreEntity.fromAggregate(score);
        jpaRepository.save(entity);
        return score;
    }

    @Override
    public List<MiniGameScore> findByUserIdAndGameType(UserProfileId userId, String gameType) {
        return jpaRepository.findByUserIdAndGameTypeOrderByPlayedAtDesc(userId.value(), gameType)
                .stream()
                .map(MiniGameScoreEntity::toAggregate)
                .toList();
    }
}
