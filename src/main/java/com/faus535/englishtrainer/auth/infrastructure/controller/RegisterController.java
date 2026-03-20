package com.faus535.englishtrainer.auth.infrastructure.controller;

import com.faus535.englishtrainer.auth.application.RegisterUserUseCase;
import com.faus535.englishtrainer.auth.domain.AuthUser;
import com.faus535.englishtrainer.auth.domain.error.EmailAlreadyExistsException;
import com.faus535.englishtrainer.auth.infrastructure.jwt.JwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class RegisterController {

    private final RegisterUserUseCase useCase;
    private final JwtService jwtService;

    RegisterController(RegisterUserUseCase useCase, JwtService jwtService) {
        this.useCase = useCase;
        this.jwtService = jwtService;
    }

    record RegisterRequest(@NotBlank @Email String email, @NotBlank @Size(min = 6) String password) {}

    record AuthResponse(String token, String refreshToken, String profileId, String email) {}

    @PostMapping("/api/auth/register")
    ResponseEntity<AuthResponse> handle(@Valid @RequestBody RegisterRequest request) throws EmailAlreadyExistsException {
        AuthUser user = useCase.execute(request.email(), request.password());

        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new AuthResponse(token, refreshToken, user.userProfileId().value().toString(), user.email()));
    }
}
