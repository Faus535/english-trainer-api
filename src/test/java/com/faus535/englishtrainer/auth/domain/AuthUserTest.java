package com.faus535.englishtrainer.auth.domain;

import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthUserTest {

    @Test
    void shouldCreateAuthUser() {
        UserProfileId profileId = UserProfileId.generate();

        AuthUser user = AuthUser.create("user@example.com", "$2a$10$hash", profileId);

        assertNotNull(user.id());
        assertEquals("user@example.com", user.email());
        assertEquals("$2a$10$hash", user.passwordHash());
        assertEquals(profileId, user.userProfileId());
        assertTrue(user.active());
        assertEquals("LOCAL", user.authProvider());
        assertFalse(user.isGoogleAccount());
        assertNotNull(user.createdAt());
        assertNotNull(user.updatedAt());
    }

    @Test
    void shouldHaveDefaultRole() {
        AuthUser user = AuthUserMother.create();

        assertEquals("USER", user.role());
    }

    @Test
    void shouldCreateGoogleAuthUser() {
        UserProfileId profileId = UserProfileId.generate();

        AuthUser user = AuthUser.createFromGoogle("google@example.com", profileId);

        assertNotNull(user.id());
        assertEquals("google@example.com", user.email());
        assertNull(user.passwordHash());
        assertEquals(profileId, user.userProfileId());
        assertEquals("USER", user.role());
        assertTrue(user.active());
        assertEquals("GOOGLE", user.authProvider());
        assertTrue(user.isGoogleAccount());
    }
}
