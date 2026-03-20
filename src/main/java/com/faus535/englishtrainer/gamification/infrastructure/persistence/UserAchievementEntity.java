package com.faus535.englishtrainer.gamification.infrastructure.persistence;

import com.faus535.englishtrainer.gamification.domain.AchievementId;
import com.faus535.englishtrainer.gamification.domain.UserAchievement;
import com.faus535.englishtrainer.gamification.domain.UserAchievementId;
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
@Table(name = "user_achievements")
class UserAchievementEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Version
    private Long version;

    @Transient
    private boolean isNew;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "achievement_id", nullable = false)
    private String achievementId;

    @Column(name = "unlocked_at", nullable = false)
    private Instant unlockedAt;

    protected UserAchievementEntity() {}

    static UserAchievementEntity fromAggregate(UserAchievement aggregate) {
        UserAchievementEntity entity = new UserAchievementEntity();
        entity.id = aggregate.id().value();
        entity.isNew = true;
        entity.userId = aggregate.userId().value();
        entity.achievementId = aggregate.achievementId().value();
        entity.unlockedAt = aggregate.unlockedAt();
        return entity;
    }

    UserAchievement toAggregate() {
        return UserAchievement.reconstitute(
                new UserAchievementId(id),
                new UserProfileId(userId),
                new AchievementId(achievementId),
                unlockedAt
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
