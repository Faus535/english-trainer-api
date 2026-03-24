package com.faus535.englishtrainer.auth.application;

import com.faus535.englishtrainer.auth.domain.AuthUserId;
import com.faus535.englishtrainer.auth.domain.RefreshToken;
import com.faus535.englishtrainer.auth.infrastructure.InMemoryRefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LogoutUserUseCaseTest {

    private InMemoryRefreshTokenRepository refreshTokenRepository;
    private LogoutUserUseCase useCase;

    @BeforeEach
    void setUp() {
        refreshTokenRepository = new InMemoryRefreshTokenRepository();
        useCase = new LogoutUserUseCase(refreshTokenRepository);
    }

    @Test
    void shouldRevokeRefreshToken() {
        AuthUserId userId = new AuthUserId(java.util.UUID.randomUUID());
        String tokenHash = "abc123hash";
        Instant expiresAt = Instant.now().plusSeconds(3600);
        RefreshToken token = RefreshToken.create(userId, tokenHash, expiresAt);
        refreshTokenRepository.save(token);

        useCase.execute(tokenHash);

        RefreshToken revoked = refreshTokenRepository.findByTokenHash(tokenHash).orElseThrow();
        assertTrue(revoked.revoked());
    }

    @Test
    void shouldBeIdempotentWhenTokenNotFound() {
        useCase.execute("nonexistent-hash");
    }

    @Test
    void shouldBeIdempotentWhenTokenAlreadyRevoked() {
        AuthUserId userId = new AuthUserId(java.util.UUID.randomUUID());
        String tokenHash = "abc123hash";
        Instant expiresAt = Instant.now().plusSeconds(3600);
        RefreshToken token = RefreshToken.create(userId, tokenHash, expiresAt);
        refreshTokenRepository.save(token.revoke());

        useCase.execute(tokenHash);

        RefreshToken stored = refreshTokenRepository.findByTokenHash(tokenHash).orElseThrow();
        assertTrue(stored.revoked());
    }

    @Test
    void shouldNotAffectOtherTokens() {
        AuthUserId userId = new AuthUserId(java.util.UUID.randomUUID());
        String tokenHash1 = "hash1";
        String tokenHash2 = "hash2";
        Instant expiresAt = Instant.now().plusSeconds(3600);
        refreshTokenRepository.save(RefreshToken.create(userId, tokenHash1, expiresAt));
        refreshTokenRepository.save(RefreshToken.create(userId, tokenHash2, expiresAt));

        useCase.execute(tokenHash1);

        assertTrue(refreshTokenRepository.findByTokenHash(tokenHash1).orElseThrow().revoked());
        assertFalse(refreshTokenRepository.findByTokenHash(tokenHash2).orElseThrow().revoked());
    }
}
