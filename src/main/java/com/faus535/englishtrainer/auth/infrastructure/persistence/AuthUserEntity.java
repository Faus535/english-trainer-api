package com.faus535.englishtrainer.auth.infrastructure.persistence;

import com.faus535.englishtrainer.auth.domain.AuthUser;
import com.faus535.englishtrainer.auth.domain.AuthUserId;
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
@Table(name = "users")
class AuthUserEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Version
    private Long version;

    @Transient
    private boolean isNew;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "user_profile_id")
    private UUID userProfileId;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected AuthUserEntity() {}

    static AuthUserEntity fromAggregate(AuthUser aggregate) {
        AuthUserEntity entity = new AuthUserEntity();
        entity.id = aggregate.id().value();
        entity.isNew = true;
        entity.email = aggregate.email();
        entity.passwordHash = aggregate.passwordHash();
        entity.userProfileId = aggregate.userProfileId().value();
        entity.role = aggregate.role();
        entity.active = aggregate.active();
        entity.createdAt = aggregate.createdAt();
        entity.updatedAt = aggregate.updatedAt();
        return entity;
    }

    AuthUser toAggregate() {
        return AuthUser.reconstitute(
                new AuthUserId(id),
                email,
                passwordHash,
                new UserProfileId(userProfileId),
                role,
                active,
                createdAt,
                updatedAt
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
