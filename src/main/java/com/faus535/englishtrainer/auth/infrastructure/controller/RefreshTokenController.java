package com.faus535.englishtrainer.auth.infrastructure.controller;

import com.faus535.englishtrainer.auth.domain.AuthUser;
import com.faus535.englishtrainer.auth.domain.AuthUserId;
import com.faus535.englishtrainer.auth.domain.AuthUserRepository;
import com.faus535.englishtrainer.auth.domain.RefreshToken;
import com.faus535.englishtrainer.auth.domain.RefreshTokenRepository;
import com.faus535.englishtrainer.auth.infrastructure.jwt.JwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Optional;

@RestController
class RefreshTokenController {

    private final JwtService jwtService;
    private final AuthUserRepository authUserRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    RefreshTokenController(JwtService jwtService, AuthUserRepository authUserRepository,
                           RefreshTokenRepository refreshTokenRepository) {
        this.jwtService = jwtService;
        this.authUserRepository = authUserRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    record RefreshRequest(@NotBlank String refreshToken) {}

    record AuthResponse(String token, String refreshToken, String profileId, String email) {}

    @PostMapping("/api/auth/refresh")
    ResponseEntity<AuthResponse> handle(@Valid @RequestBody RefreshRequest request) {
        if (!jwtService.isTokenValid(request.refreshToken())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String tokenHash = hashToken(request.refreshToken());
        Optional<RefreshToken> storedToken = refreshTokenRepository.findByTokenHash(tokenHash);

        if (storedToken.isPresent() && !storedToken.get().isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String userId = jwtService.extractUserId(request.refreshToken());
        AuthUserId authUserId = AuthUserId.fromString(userId);

        AuthUser user = authUserRepository.findById(authUserId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Revoke old token if it was tracked
        storedToken.ifPresent(t -> {
            refreshTokenRepository.save(t.revoke());
        });

        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        // Store new refresh token
        String newTokenHash = hashToken(newRefreshToken);
        Instant expiresAt = Instant.now().plusMillis(jwtService.getRefreshExpiration());
        RefreshToken newStoredToken = RefreshToken.create(authUserId, newTokenHash, expiresAt);
        refreshTokenRepository.save(newStoredToken);

        return ResponseEntity.ok(
                new AuthResponse(newAccessToken, newRefreshToken,
                        user.userProfileId().value().toString(), user.email()));
    }

    static String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
