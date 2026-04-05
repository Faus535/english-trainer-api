package com.faus535.englishtrainer.user.infrastructure.persistence;

import com.faus535.englishtrainer.user.domain.UserProfile;
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
@Table(name = "user_profiles")
class UserProfileEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Version
    private Long version;

    @Transient
    private boolean isNew;

    @Column(nullable = false)
    private int xp;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected UserProfileEntity() {}

    static UserProfileEntity fromAggregate(UserProfile aggregate) {
        UserProfileEntity entity = new UserProfileEntity();
        entity.id = aggregate.id().value();
        entity.version = aggregate.version();
        entity.isNew = aggregate.version() == null;
        entity.xp = aggregate.xp();
        entity.createdAt = aggregate.createdAt();
        entity.updatedAt = aggregate.updatedAt();
        return entity;
    }

    UserProfile toAggregate() {
        return UserProfile.reconstitute(
                new UserProfileId(id),
                version,
                xp,
                createdAt,
                updatedAt
        );
    }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}
