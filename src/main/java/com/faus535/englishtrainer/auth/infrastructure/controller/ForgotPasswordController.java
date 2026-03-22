package com.faus535.englishtrainer.auth.infrastructure.controller;

import com.faus535.englishtrainer.auth.application.ForgotPasswordUseCase;
import com.faus535.englishtrainer.auth.domain.error.TooManyResetAttemptsException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ForgotPasswordController {

    private final ForgotPasswordUseCase useCase;

    ForgotPasswordController(ForgotPasswordUseCase useCase) {
        this.useCase = useCase;
    }

    record ForgotPasswordRequest(@NotBlank @Email String email) {}

    @PostMapping("/api/auth/forgot-password")
    ResponseEntity<Void> handle(@Valid @RequestBody ForgotPasswordRequest request) throws TooManyResetAttemptsException {
        useCase.execute(request.email());
        return ResponseEntity.ok().build();
    }
}
