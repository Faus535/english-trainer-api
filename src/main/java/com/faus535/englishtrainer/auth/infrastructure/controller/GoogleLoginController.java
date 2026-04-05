package com.faus535.englishtrainer.auth.infrastructure.controller;

import com.faus535.englishtrainer.auth.application.GoogleLoginUseCase;
import com.faus535.englishtrainer.auth.domain.AuthUser;
import com.faus535.englishtrainer.auth.domain.RefreshToken;
import com.faus535.englishtrainer.auth.domain.RefreshTokenRepository;
import com.faus535.englishtrainer.auth.domain.error.GoogleAuthException;
import com.faus535.englishtrainer.auth.infrastructure.jwt.JwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
class GoogleLoginController {

    private final GoogleLoginUseCase useCase;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    GoogleLoginController(GoogleLoginUseCase useCase, JwtService jwtService,
                          RefreshTokenRepository refreshTokenRepository) {
        this.useCase = useCase;
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    record GoogleLoginRequest(@NotBlank(message = "idToken is required") String idToken) {}

    record AuthResponse(String token, String refreshToken, String profileId, String email) {}

    @PostMapping("/api/auth/google")
    ResponseEntity<AuthResponse> handle(@Valid @RequestBody GoogleLoginRequest request) throws GoogleAuthException {
        AuthUser user = useCase.execute(request.idToken());

        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        String tokenHash = com.faus535.englishtrainer.auth.application.RefreshTokenUseCase.hashToken(refreshToken);
        Instant expiresAt = Instant.now().plusMillis(jwtService.getRefreshExpiration());
        refreshTokenRepository.save(RefreshToken.create(user.id(), tokenHash, expiresAt));

        return ResponseEntity.ok(
                new AuthResponse(token, refreshToken, user.userProfileId().value().toString(), user.email()));
    }
}
