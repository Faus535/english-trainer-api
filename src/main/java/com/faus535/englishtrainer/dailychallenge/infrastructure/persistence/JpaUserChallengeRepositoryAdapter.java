package com.faus535.englishtrainer.dailychallenge.infrastructure.persistence;

import com.faus535.englishtrainer.dailychallenge.domain.DailyChallengeId;
import com.faus535.englishtrainer.dailychallenge.domain.UserChallenge;
import com.faus535.englishtrainer.dailychallenge.domain.UserChallengeRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
class JpaUserChallengeRepositoryAdapter implements UserChallengeRepository {

    private final JpaUserChallengeRepository jpaRepository;

    JpaUserChallengeRepositoryAdapter(JpaUserChallengeRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<UserChallenge> findByUserIdAndChallengeId(UserProfileId userId, DailyChallengeId challengeId) {
        return jpaRepository.findByUserIdAndChallengeId(userId.value(), challengeId.value())
                .map(UserChallengeEntity::toAggregate);
    }

    @Override
    public UserChallenge save(UserChallenge userChallenge) {
        UserChallengeEntity entity = UserChallengeEntity.fromAggregate(userChallenge);
        if (jpaRepository.existsById(userChallenge.id().value())) {
            entity.markAsExisting();
        }
        return jpaRepository.save(entity).toAggregate();
    }
}
