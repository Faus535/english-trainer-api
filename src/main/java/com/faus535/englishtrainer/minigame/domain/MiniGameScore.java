package com.faus535.englishtrainer.minigame.domain;

import com.faus535.englishtrainer.shared.domain.AggregateRoot;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.time.LocalDateTime;

public final class MiniGameScore extends AggregateRoot<MiniGameScoreId> {

    private final MiniGameScoreId id;
    private final UserProfileId userId;
    private final String gameType;
    private final int score;
    private final int xpEarned;
    private final LocalDateTime playedAt;

    private MiniGameScore(MiniGameScoreId id, UserProfileId userId, String gameType, int score, int xpEarned, LocalDateTime playedAt) {
        this.id = id;
        this.userId = userId;
        this.gameType = gameType;
        this.score = score;
        this.xpEarned = xpEarned;
        this.playedAt = playedAt;
    }

    public static MiniGameScore create(UserProfileId userId, String gameType, int score, int xpEarned) {
        return new MiniGameScore(
                MiniGameScoreId.generate(),
                userId,
                gameType,
                score,
                xpEarned,
                LocalDateTime.now()
        );
    }

    public static MiniGameScore reconstitute(MiniGameScoreId id, UserProfileId userId, String gameType, int score, int xpEarned, LocalDateTime playedAt) {
        return new MiniGameScore(id, userId, gameType, score, xpEarned, playedAt);
    }

    public MiniGameScoreId id() { return id; }
    public UserProfileId userId() { return userId; }
    public String gameType() { return gameType; }
    public int score() { return score; }
    public int xpEarned() { return xpEarned; }
    public LocalDateTime playedAt() { return playedAt; }
}
