package com.faus535.englishtrainer.auth.domain;

import java.time.Instant;
import java.util.UUID;

public final class PasswordResetToken {

    private static final long EXPIRATION_MINUTES = 15;

    private final UUID id;
    private final AuthUserId userId;
    private final String tokenHash;
    private final Instant expiresAt;
    private final boolean used;
    private final Instant createdAt;

    private PasswordResetToken(UUID id, AuthUserId userId, String tokenHash, Instant expiresAt,
                                boolean used, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.used = used;
        this.createdAt = createdAt;
    }

    public static PasswordResetToken create(AuthUserId userId, String tokenHash) {
        Instant now = Instant.now();
        return new PasswordResetToken(
                UUID.randomUUID(), userId, tokenHash,
                now.plusSeconds(EXPIRATION_MINUTES * 60), false, now);
    }

    public static PasswordResetToken reconstitute(UUID id, AuthUserId userId, String tokenHash,
                                                    Instant expiresAt, boolean used, Instant createdAt) {
        return new PasswordResetToken(id, userId, tokenHash, expiresAt, used, createdAt);
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return !used && !isExpired();
    }

    public PasswordResetToken markUsed() {
        return new PasswordResetToken(id, userId, tokenHash, expiresAt, true, createdAt);
    }

    public UUID id() { return id; }
    public AuthUserId userId() { return userId; }
    public String tokenHash() { return tokenHash; }
    public Instant expiresAt() { return expiresAt; }
    public boolean used() { return used; }
    public Instant createdAt() { return createdAt; }
}
