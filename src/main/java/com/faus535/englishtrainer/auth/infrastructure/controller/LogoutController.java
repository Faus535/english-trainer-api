package com.faus535.englishtrainer.auth.infrastructure.controller;

import com.faus535.englishtrainer.auth.application.LogoutUserUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class LogoutController {

    private final LogoutUserUseCase useCase;

    LogoutController(LogoutUserUseCase useCase) {
        this.useCase = useCase;
    }

    record LogoutRequest(@NotBlank String refreshToken) {}

    @PostMapping("/api/auth/logout")
    ResponseEntity<Void> handle(@Valid @RequestBody LogoutRequest request) {
        String tokenHash = RefreshTokenController.hashToken(request.refreshToken());
        useCase.execute(tokenHash);
        return ResponseEntity.noContent().build();
    }
}
