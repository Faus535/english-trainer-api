package com.faus535.englishtrainer.dailychallenge.domain;

import com.faus535.englishtrainer.dailychallenge.domain.event.ChallengeCompletedEvent;
import com.faus535.englishtrainer.shared.domain.AggregateRoot;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.time.Instant;

public final class UserChallenge extends AggregateRoot<UserChallengeId> {

    private final UserChallengeId id;
    private final UserProfileId userId;
    private final DailyChallengeId challengeId;
    private final int progress;
    private final boolean completed;
    private final Instant completedAt;

    private UserChallenge(UserChallengeId id, UserProfileId userId, DailyChallengeId challengeId,
                          int progress, boolean completed, Instant completedAt) {
        this.id = id;
        this.userId = userId;
        this.challengeId = challengeId;
        this.progress = progress;
        this.completed = completed;
        this.completedAt = completedAt;
    }

    public static UserChallenge create(UserChallengeId id, UserProfileId userId, DailyChallengeId challengeId) {
        return new UserChallenge(id, userId, challengeId, 0, false, null);
    }

    public static UserChallenge reconstitute(UserChallengeId id, UserProfileId userId, DailyChallengeId challengeId,
                                              int progress, boolean completed, Instant completedAt) {
        return new UserChallenge(id, userId, challengeId, progress, completed, completedAt);
    }

    public UserChallenge updateProgress(int newProgress) {
        if (this.completed) {
            return this;
        }
        return new UserChallenge(id, userId, challengeId, newProgress, completed, completedAt);
    }

    public UserChallenge complete() {
        if (this.completed) {
            return this;
        }
        UserChallenge completedChallenge = new UserChallenge(id, userId, challengeId, progress, true, Instant.now());
        completedChallenge.registerEvent(new ChallengeCompletedEvent(id, userId, challengeId));
        return completedChallenge;
    }

    public UserChallengeId id() { return id; }
    public UserProfileId userId() { return userId; }
    public DailyChallengeId challengeId() { return challengeId; }
    public int progress() { return progress; }
    public boolean completed() { return completed; }
    public Instant completedAt() { return completedAt; }
}
