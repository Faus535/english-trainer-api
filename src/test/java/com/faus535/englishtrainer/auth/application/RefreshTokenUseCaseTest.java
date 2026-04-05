package com.faus535.englishtrainer.auth.application;

import com.faus535.englishtrainer.auth.domain.AuthUser;
import com.faus535.englishtrainer.auth.domain.AuthUserMother;
import com.faus535.englishtrainer.auth.domain.RefreshToken;
import com.faus535.englishtrainer.auth.domain.error.InvalidRefreshTokenException;
import com.faus535.englishtrainer.auth.infrastructure.InMemoryAuthUserRepository;
import com.faus535.englishtrainer.auth.infrastructure.InMemoryRefreshTokenRepository;
import com.faus535.englishtrainer.auth.infrastructure.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RefreshTokenUseCaseTest {

    private JwtService jwtService;
    private InMemoryAuthUserRepository authUserRepository;
    private InMemoryRefreshTokenRepository refreshTokenRepository;
    private RefreshTokenUseCase useCase;

    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);
        authUserRepository = new InMemoryAuthUserRepository();
        refreshTokenRepository = new InMemoryRefreshTokenRepository();
        useCase = new RefreshTokenUseCase(jwtService, authUserRepository, refreshTokenRepository);
    }

    @Test
    void shouldRefreshTokenSuccessfully() throws Exception {
        AuthUser user = AuthUserMother.create();
        authUserRepository.save(user);

        String oldRefreshToken = "old-refresh-token";
        String tokenHash = RefreshTokenUseCase.hashToken(oldRefreshToken);
        refreshTokenRepository.save(RefreshToken.create(user.id(), tokenHash, Instant.now().plusSeconds(3600)));

        when(jwtService.isTokenValid(oldRefreshToken)).thenReturn(true);
        when(jwtService.extractUserId(oldRefreshToken)).thenReturn(user.id().value().toString());
        when(jwtService.generateToken(user)).thenReturn("new-access-token");
        when(jwtService.generateRefreshToken(user)).thenReturn("new-refresh-token");
        when(jwtService.getRefreshExpiration()).thenReturn(86400000L);

        RefreshTokenUseCase.RefreshResult result = useCase.execute(oldRefreshToken);

        assertEquals("new-access-token", result.accessToken());
        assertEquals("new-refresh-token", result.refreshToken());
        assertEquals(user.email(), result.email());
        assertTrue(refreshTokenRepository.findByTokenHash(tokenHash).get().revoked());
    }

    @Test
    void shouldThrowForInvalidJwt() {
        when(jwtService.isTokenValid("invalid-token")).thenReturn(false);

        assertThrows(InvalidRefreshTokenException.class,
                () -> useCase.execute("invalid-token"));
    }

    @Test
    void shouldThrowForRevokedToken() {
        AuthUser user = AuthUserMother.create();
        authUserRepository.save(user);

        String revokedToken = "revoked-token";
        String tokenHash = RefreshTokenUseCase.hashToken(revokedToken);
        RefreshToken token = RefreshToken.create(user.id(), tokenHash, Instant.now().plusSeconds(3600)).revoke();
        refreshTokenRepository.save(token);

        when(jwtService.isTokenValid(revokedToken)).thenReturn(true);

        assertThrows(InvalidRefreshTokenException.class,
                () -> useCase.execute(revokedToken));
    }

    @Test
    void shouldThrowForMissingUser() {
        String validToken = "valid-but-orphan";
        when(jwtService.isTokenValid(validToken)).thenReturn(true);
        when(jwtService.extractUserId(validToken)).thenReturn(java.util.UUID.randomUUID().toString());

        assertThrows(InvalidRefreshTokenException.class,
                () -> useCase.execute(validToken));
    }
}
