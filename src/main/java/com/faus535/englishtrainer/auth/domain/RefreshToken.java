package com.faus535.englishtrainer.auth.domain;

import java.time.Instant;
import java.util.UUID;

public final class RefreshToken {

    private final UUID id;
    private final AuthUserId userId;
    private final String tokenHash;
    private final Instant expiresAt;
    private final boolean revoked;
    private final Instant createdAt;

    private RefreshToken(UUID id, AuthUserId userId, String tokenHash, Instant expiresAt,
                         boolean revoked, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
        this.createdAt = createdAt;
    }

    public static RefreshToken create(AuthUserId userId, String tokenHash, Instant expiresAt) {
        return new RefreshToken(UUID.randomUUID(), userId, tokenHash, expiresAt, false, Instant.now());
    }

    public static RefreshToken reconstitute(UUID id, AuthUserId userId, String tokenHash,
                                             Instant expiresAt, boolean revoked, Instant createdAt) {
        return new RefreshToken(id, userId, tokenHash, expiresAt, revoked, createdAt);
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return !revoked && !isExpired();
    }

    public RefreshToken revoke() {
        return new RefreshToken(id, userId, tokenHash, expiresAt, true, createdAt);
    }

    public UUID id() { return id; }
    public AuthUserId userId() { return userId; }
    public String tokenHash() { return tokenHash; }
    public Instant expiresAt() { return expiresAt; }
    public boolean revoked() { return revoked; }
    public Instant createdAt() { return createdAt; }
}
