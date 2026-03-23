package com.faus535.englishtrainer.dailychallenge.infrastructure.persistence;

import com.faus535.englishtrainer.dailychallenge.domain.DailyChallengeId;
import com.faus535.englishtrainer.dailychallenge.domain.UserChallenge;
import com.faus535.englishtrainer.dailychallenge.domain.UserChallengeId;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_challenges")
class UserChallengeEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Version
    private Long version;

    @Transient
    private boolean isNew;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "challenge_id", nullable = false)
    private UUID challengeId;

    @Column(nullable = false)
    private int progress;

    @Column(nullable = false)
    private boolean completed;

    @Column(name = "completed_at")
    private Instant completedAt;

    protected UserChallengeEntity() {}

    static UserChallengeEntity fromAggregate(UserChallenge aggregate) {
        UserChallengeEntity entity = new UserChallengeEntity();
        entity.id = aggregate.id().value();
        entity.isNew = true;
        entity.userId = aggregate.userId().value();
        entity.challengeId = aggregate.challengeId().value();
        entity.progress = aggregate.progress();
        entity.completed = aggregate.completed();
        entity.completedAt = aggregate.completedAt();
        return entity;
    }

    UserChallenge toAggregate() {
        return UserChallenge.reconstitute(
                new UserChallengeId(id),
                new UserProfileId(userId),
                new DailyChallengeId(challengeId),
                progress,
                completed,
                completedAt
        );
    }

    void markAsExisting() {
        this.isNew = false;
    }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}
