package com.faus535.englishtrainer.gamification.infrastructure.persistence;

import com.faus535.englishtrainer.gamification.domain.Achievement;
import com.faus535.englishtrainer.gamification.domain.AchievementId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "achievements")
class AchievementEntity implements Persistable<String> {

    @Id
    private String id;

    @Version
    private Long version;

    @Transient
    private boolean isNew;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    private String icon;

    @Column(name = "xp_reward", nullable = false)
    private int xpReward;

    protected AchievementEntity() {}

    static AchievementEntity fromAggregate(Achievement aggregate) {
        AchievementEntity entity = new AchievementEntity();
        entity.id = aggregate.id().value();
        entity.isNew = true;
        entity.name = aggregate.name();
        entity.description = aggregate.description();
        entity.icon = aggregate.icon();
        entity.xpReward = aggregate.xpReward();
        return entity;
    }

    Achievement toAggregate() {
        return Achievement.reconstitute(
                new AchievementId(id),
                name,
                description,
                icon,
                xpReward
        );
    }

    void markAsExisting() {
        this.isNew = false;
    }

    @Override
    public String getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}
