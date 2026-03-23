package com.faus535.englishtrainer.minigame.infrastructure.persistence;

import com.faus535.englishtrainer.minigame.domain.MiniGameScore;
import com.faus535.englishtrainer.minigame.domain.MiniGameScoreId;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "mini_game_scores")
class MiniGameScoreEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Version
    private Long version;

    @Transient
    private boolean isNew;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "game_type", nullable = false)
    private String gameType;

    @Column(nullable = false)
    private int score;

    @Column(name = "xp_earned", nullable = false)
    private int xpEarned;

    @Column(name = "played_at", nullable = false)
    private LocalDateTime playedAt;

    protected MiniGameScoreEntity() {}

    static MiniGameScoreEntity fromAggregate(MiniGameScore aggregate) {
        MiniGameScoreEntity entity = new MiniGameScoreEntity();
        entity.id = aggregate.id().value();
        entity.isNew = true;
        entity.userId = aggregate.userId().value();
        entity.gameType = aggregate.gameType();
        entity.score = aggregate.score();
        entity.xpEarned = aggregate.xpEarned();
        entity.playedAt = aggregate.playedAt();
        return entity;
    }

    MiniGameScore toAggregate() {
        return MiniGameScore.reconstitute(
                new MiniGameScoreId(id),
                new UserProfileId(userId),
                gameType,
                score,
                xpEarned,
                playedAt
        );
    }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}
