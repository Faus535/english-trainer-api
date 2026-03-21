package com.faus535.englishtrainer.auth.domain;

import com.faus535.englishtrainer.user.domain.UserProfileId;

public final class AuthUserMother {

    private AuthUserMother() {
    }

    public static AuthUser create() {
        return AuthUser.create(
                "test@example.com",
                "$2a$10$hashedpassword",
                UserProfileId.generate()
        );
    }

    public static AuthUser withEmail(String email) {
        return AuthUser.create(
                email,
                "$2a$10$hashedpassword",
                UserProfileId.generate()
        );
    }

    public static AuthUser withProfileId(UserProfileId profileId) {
        return AuthUser.create(
                "test@example.com",
                "$2a$10$hashedpassword",
                profileId
        );
    }

    public static AuthUser googleUser() {
        return AuthUser.createFromGoogle(
                "google@example.com",
                UserProfileId.generate()
        );
    }

    public static AuthUser googleUserWithEmail(String email) {
        return AuthUser.createFromGoogle(
                email,
                UserProfileId.generate()
        );
    }
}
