package com.faus535.englishtrainer.auth.infrastructure.controller;

import com.faus535.englishtrainer.auth.application.LoginUserUseCase;
import com.faus535.englishtrainer.auth.domain.AuthUser;
import com.faus535.englishtrainer.auth.domain.error.InvalidCredentialsException;
import com.faus535.englishtrainer.auth.infrastructure.jwt.JwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class LoginController {

    private final LoginUserUseCase useCase;
    private final JwtService jwtService;

    LoginController(LoginUserUseCase useCase, JwtService jwtService) {
        this.useCase = useCase;
        this.jwtService = jwtService;
    }

    record LoginRequest(@NotBlank String email, @NotBlank String password) {}

    record AuthResponse(String token, String refreshToken, String profileId, String email) {}

    @PostMapping("/api/auth/login")
    ResponseEntity<AuthResponse> handle(@Valid @RequestBody LoginRequest request) throws InvalidCredentialsException {
        AuthUser user = useCase.execute(request.email(), request.password());

        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return ResponseEntity.ok(
                new AuthResponse(token, refreshToken, user.userProfileId().value().toString(), user.email()));
    }
}
