package com.faus535.englishtrainer.auth.infrastructure.controller;

import com.faus535.englishtrainer.auth.application.LoginUserUseCase;
import com.faus535.englishtrainer.auth.domain.AuthUser;
import com.faus535.englishtrainer.auth.domain.RefreshToken;
import com.faus535.englishtrainer.auth.domain.RefreshTokenRepository;
import com.faus535.englishtrainer.auth.domain.error.AccountUsesGoogleException;
import com.faus535.englishtrainer.auth.domain.error.InvalidCredentialsException;
import com.faus535.englishtrainer.auth.infrastructure.jwt.JwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
class LoginController {

    private final LoginUserUseCase useCase;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    LoginController(LoginUserUseCase useCase, JwtService jwtService,
                    RefreshTokenRepository refreshTokenRepository) {
        this.useCase = useCase;
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    record LoginRequest(@NotBlank String email, @NotBlank String password) {}

    record AuthResponse(String token, String refreshToken, String profileId, String email) {}

    @PostMapping("/api/auth/login")
    ResponseEntity<AuthResponse> handle(@Valid @RequestBody LoginRequest request)
            throws InvalidCredentialsException, AccountUsesGoogleException {

        AuthUser user = useCase.execute(request.email(), request.password());

        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        String tokenHash = com.faus535.englishtrainer.auth.application.RefreshTokenUseCase.hashToken(refreshToken);
        Instant expiresAt = Instant.now().plusMillis(jwtService.getRefreshExpiration());
        refreshTokenRepository.save(RefreshToken.create(user.id(), tokenHash, expiresAt));

        return ResponseEntity.ok(
                new AuthResponse(token, refreshToken, user.userProfileId().value().toString(), user.email()));
    }
}
