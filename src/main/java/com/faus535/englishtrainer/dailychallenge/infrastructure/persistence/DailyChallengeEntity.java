package com.faus535.englishtrainer.dailychallenge.infrastructure.persistence;

import com.faus535.englishtrainer.dailychallenge.domain.ChallengeType;
import com.faus535.englishtrainer.dailychallenge.domain.DailyChallenge;
import com.faus535.englishtrainer.dailychallenge.domain.DailyChallengeId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import org.springframework.data.domain.Persistable;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "daily_challenges")
class DailyChallengeEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Version
    private Long version;

    @Transient
    private boolean isNew;

    @Enumerated(EnumType.STRING)
    @Column(name = "challenge_type", nullable = false)
    private ChallengeType challengeType;

    @Column(name = "description_es", nullable = false)
    private String descriptionEs;

    @Column(name = "description_en", nullable = false)
    private String descriptionEn;

    @Column(nullable = false)
    private int target;

    @Column(name = "xp_reward", nullable = false)
    private int xpReward;

    @Column(name = "challenge_date", nullable = false)
    private LocalDate challengeDate;

    protected DailyChallengeEntity() {}

    static DailyChallengeEntity fromAggregate(DailyChallenge aggregate) {
        DailyChallengeEntity entity = new DailyChallengeEntity();
        entity.id = aggregate.id().value();
        entity.isNew = true;
        entity.challengeType = aggregate.challengeType();
        entity.descriptionEs = aggregate.descriptionEs();
        entity.descriptionEn = aggregate.descriptionEn();
        entity.target = aggregate.target();
        entity.xpReward = aggregate.xpReward();
        entity.challengeDate = aggregate.challengeDate();
        return entity;
    }

    DailyChallenge toAggregate() {
        return DailyChallenge.reconstitute(
                new DailyChallengeId(id),
                challengeType,
                descriptionEs,
                descriptionEn,
                target,
                xpReward,
                challengeDate
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
