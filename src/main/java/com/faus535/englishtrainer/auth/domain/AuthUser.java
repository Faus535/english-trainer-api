package com.faus535.englishtrainer.auth.domain;

import com.faus535.englishtrainer.shared.domain.AggregateRoot;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.time.Instant;

public final class AuthUser extends AggregateRoot<AuthUserId> {

    private final AuthUserId id;
    private final String email;
    private final String passwordHash;
    private final UserProfileId userProfileId;
    private final String role;
    private final boolean active;
    private final String authProvider;
    private final Instant createdAt;
    private final Instant updatedAt;

    private AuthUser(AuthUserId id, String email, String passwordHash, UserProfileId userProfileId,
                     String role, boolean active, String authProvider, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.userProfileId = userProfileId;
        this.role = role;
        this.active = active;
        this.authProvider = authProvider;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static AuthUser create(String email, String passwordHash, UserProfileId profileId) {
        Instant now = Instant.now();
        return new AuthUser(
                AuthUserId.generate(),
                email,
                passwordHash,
                profileId,
                "USER",
                true,
                "LOCAL",
                now,
                now
        );
    }

    public static AuthUser createFromGoogle(String email, UserProfileId profileId) {
        Instant now = Instant.now();
        return new AuthUser(
                AuthUserId.generate(),
                email,
                null,
                profileId,
                "USER",
                true,
                "GOOGLE",
                now,
                now
        );
    }

    public static AuthUser reconstitute(AuthUserId id, String email, String passwordHash,
                                        UserProfileId userProfileId, String role, boolean active,
                                        String authProvider, Instant createdAt, Instant updatedAt) {
        return new AuthUser(id, email, passwordHash, userProfileId, role, active, authProvider, createdAt, updatedAt);
    }

    public boolean isGoogleAccount() {
        return "GOOGLE".equals(authProvider);
    }

    public AuthUserId id() { return id; }
    public String email() { return email; }
    public String passwordHash() { return passwordHash; }
    public UserProfileId userProfileId() { return userProfileId; }
    public String role() { return role; }
    public boolean active() { return active; }
    public String authProvider() { return authProvider; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}
