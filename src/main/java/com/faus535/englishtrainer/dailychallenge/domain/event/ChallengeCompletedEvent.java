package com.faus535.englishtrainer.dailychallenge.domain.event;

import com.faus535.englishtrainer.dailychallenge.domain.DailyChallengeId;
import com.faus535.englishtrainer.dailychallenge.domain.UserChallengeId;
import com.faus535.englishtrainer.user.domain.UserProfileId;

public record ChallengeCompletedEvent(UserChallengeId userChallengeId, UserProfileId userId, DailyChallengeId challengeId) {}
