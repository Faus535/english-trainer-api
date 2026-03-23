package com.faus535.englishtrainer.dailychallenge.domain;

import com.faus535.englishtrainer.shared.domain.AggregateRoot;

import java.time.LocalDate;

public final class DailyChallenge extends AggregateRoot<DailyChallengeId> {

    private final DailyChallengeId id;
    private final ChallengeType challengeType;
    private final String descriptionEs;
    private final String descriptionEn;
    private final int target;
    private final int xpReward;
    private final LocalDate challengeDate;

    private DailyChallenge(DailyChallengeId id, ChallengeType challengeType, String descriptionEs,
                           String descriptionEn, int target, int xpReward, LocalDate challengeDate) {
        this.id = id;
        this.challengeType = challengeType;
        this.descriptionEs = descriptionEs;
        this.descriptionEn = descriptionEn;
        this.target = target;
        this.xpReward = xpReward;
        this.challengeDate = challengeDate;
    }

    public static DailyChallenge create(DailyChallengeId id, ChallengeType challengeType, String descriptionEs,
                                         String descriptionEn, int target, int xpReward, LocalDate challengeDate) {
        return new DailyChallenge(id, challengeType, descriptionEs, descriptionEn, target, xpReward, challengeDate);
    }

    public static DailyChallenge reconstitute(DailyChallengeId id, ChallengeType challengeType, String descriptionEs,
                                               String descriptionEn, int target, int xpReward, LocalDate challengeDate) {
        return new DailyChallenge(id, challengeType, descriptionEs, descriptionEn, target, xpReward, challengeDate);
    }

    public DailyChallengeId id() { return id; }
    public ChallengeType challengeType() { return challengeType; }
    public String descriptionEs() { return descriptionEs; }
    public String descriptionEn() { return descriptionEn; }
    public int target() { return target; }
    public int xpReward() { return xpReward; }
    public LocalDate challengeDate() { return challengeDate; }
}
