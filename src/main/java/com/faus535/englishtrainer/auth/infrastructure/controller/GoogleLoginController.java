package com.faus535.englishtrainer.auth.infrastructure.controller;

import com.faus535.englishtrainer.auth.application.GoogleLoginUseCase;
import com.faus535.englishtrainer.auth.domain.AuthUser;
import com.faus535.englishtrainer.auth.domain.error.GoogleAuthException;
import com.faus535.englishtrainer.auth.infrastructure.jwt.JwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class GoogleLoginController {

    private final GoogleLoginUseCase useCase;
    private final JwtService jwtService;

    GoogleLoginController(GoogleLoginUseCase useCase, JwtService jwtService) {
        this.useCase = useCase;
        this.jwtService = jwtService;
    }

    record GoogleLoginRequest(@NotBlank(message = "idToken is required") String idToken) {}

    record AuthResponse(String token, String refreshToken, String profileId, String email) {}

    @PostMapping("/api/auth/google")
    ResponseEntity<AuthResponse> handle(@Valid @RequestBody GoogleLoginRequest request) throws GoogleAuthException {
        AuthUser user = useCase.execute(request.idToken());

        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return ResponseEntity.ok(
                new AuthResponse(token, refreshToken, user.userProfileId().value().toString(), user.email()));
    }
}
