package com.faus535.englishtrainer.auth.infrastructure.controller;

import com.faus535.englishtrainer.auth.application.RefreshTokenUseCase;
import com.faus535.englishtrainer.auth.domain.error.InvalidRefreshTokenException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class RefreshTokenController {

    private final RefreshTokenUseCase useCase;

    RefreshTokenController(RefreshTokenUseCase useCase) {
        this.useCase = useCase;
    }

    record RefreshRequest(@NotBlank String refreshToken) {}

    record AuthResponse(String token, String refreshToken, String profileId, String email) {}

    @PostMapping("/api/auth/refresh")
    ResponseEntity<AuthResponse> handle(@Valid @RequestBody RefreshRequest request) {
        try {
            RefreshTokenUseCase.RefreshResult result = useCase.execute(request.refreshToken());
            return ResponseEntity.ok(
                    new AuthResponse(result.accessToken(), result.refreshToken(),
                            result.profileId(), result.email()));
        } catch (InvalidRefreshTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
