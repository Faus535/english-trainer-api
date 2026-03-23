package com.faus535.englishtrainer.dailychallenge.domain;

import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.util.Optional;

public interface UserChallengeRepository {

    Optional<UserChallenge> findByUserIdAndChallengeId(UserProfileId userId, DailyChallengeId challengeId);

    UserChallenge save(UserChallenge userChallenge);
}
