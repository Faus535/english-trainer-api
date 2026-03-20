package com.faus535.englishtrainer.auth.infrastructure.controller;

import com.faus535.englishtrainer.auth.domain.AuthUser;
import com.faus535.englishtrainer.auth.domain.AuthUserId;
import com.faus535.englishtrainer.auth.domain.AuthUserRepository;
import com.faus535.englishtrainer.auth.infrastructure.jwt.JwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class RefreshTokenController {

    private final JwtService jwtService;
    private final AuthUserRepository authUserRepository;

    RefreshTokenController(JwtService jwtService, AuthUserRepository authUserRepository) {
        this.jwtService = jwtService;
        this.authUserRepository = authUserRepository;
    }

    record RefreshRequest(@NotBlank String refreshToken) {}

    record AuthResponse(String token, String refreshToken, String profileId, String email) {}

    @PostMapping("/api/auth/refresh")
    ResponseEntity<AuthResponse> handle(@Valid @RequestBody RefreshRequest request) {
        if (!jwtService.isTokenValid(request.refreshToken())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String userId = jwtService.extractUserId(request.refreshToken());
        AuthUserId authUserId = AuthUserId.fromString(userId);

        AuthUser user = authUserRepository.findById(authUserId)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return ResponseEntity.ok(
                new AuthResponse(token, refreshToken, user.userProfileId().value().toString(), user.email()));
    }
}
