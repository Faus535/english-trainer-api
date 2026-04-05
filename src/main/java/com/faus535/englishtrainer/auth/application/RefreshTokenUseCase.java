package com.faus535.englishtrainer.auth.application;

import com.faus535.englishtrainer.auth.domain.*;
import com.faus535.englishtrainer.auth.domain.error.InvalidRefreshTokenException;
import com.faus535.englishtrainer.auth.infrastructure.jwt.JwtService;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Optional;

@UseCase
public class RefreshTokenUseCase {

    private static final Logger log = LoggerFactory.getLogger(RefreshTokenUseCase.class);

    private final JwtService jwtService;
    private final AuthUserRepository authUserRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    RefreshTokenUseCase(JwtService jwtService,
                        AuthUserRepository authUserRepository,
                        RefreshTokenRepository refreshTokenRepository) {
        this.jwtService = jwtService;
        this.authUserRepository = authUserRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public RefreshResult execute(String refreshToken) throws InvalidRefreshTokenException {
        if (!jwtService.isTokenValid(refreshToken)) {
            throw new InvalidRefreshTokenException();
        }

        String tokenHash = hashToken(refreshToken);
        Optional<RefreshToken> storedToken = refreshTokenRepository.findByTokenHash(tokenHash);

        if (storedToken.isPresent() && !storedToken.get().isValid()) {
            throw new InvalidRefreshTokenException();
        }

        String userId = jwtService.extractUserId(refreshToken);
        AuthUserId authUserId = AuthUserId.fromString(userId);

        AuthUser user = authUserRepository.findById(authUserId)
                .orElseThrow(InvalidRefreshTokenException::new);

        storedToken.ifPresent(t -> refreshTokenRepository.save(t.revoke()));

        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        String newTokenHash = hashToken(newRefreshToken);
        Instant expiresAt = Instant.now().plusMillis(jwtService.getRefreshExpiration());
        com.faus535.englishtrainer.auth.domain.RefreshToken newStoredToken =
                com.faus535.englishtrainer.auth.domain.RefreshToken.create(authUserId, newTokenHash, expiresAt);
        try {
            refreshTokenRepository.save(newStoredToken);
        } catch (DataIntegrityViolationException e) {
            log.warn("Concurrent refresh token request detected for user {}", authUserId);
            throw new InvalidRefreshTokenException();
        }

        return new RefreshResult(newAccessToken, newRefreshToken,
                user.userProfileId().value().toString(), user.email());
    }

    public static String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    public record RefreshResult(String accessToken, String refreshToken, String profileId, String email) {}
}
