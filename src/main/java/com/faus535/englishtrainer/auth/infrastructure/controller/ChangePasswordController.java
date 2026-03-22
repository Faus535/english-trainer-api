package com.faus535.englishtrainer.auth.infrastructure.controller;

import com.faus535.englishtrainer.auth.application.ChangePasswordUseCase;
import com.faus535.englishtrainer.auth.domain.error.AccountUsesGoogleException;
import com.faus535.englishtrainer.auth.domain.error.InvalidCredentialsException;
import com.faus535.englishtrainer.shared.domain.error.NotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ChangePasswordController {

    private final ChangePasswordUseCase useCase;

    ChangePasswordController(ChangePasswordUseCase useCase) {
        this.useCase = useCase;
    }

    record ChangePasswordRequest(
            @NotBlank String currentPassword,
            @NotBlank @Size(min = 6) String newPassword
    ) {}

    @PutMapping("/api/auth/change-password")
    ResponseEntity<Void> handle(@Valid @RequestBody ChangePasswordRequest request,
                                Authentication authentication)
            throws NotFoundException, InvalidCredentialsException, AccountUsesGoogleException {

        String userId = authentication.getName();
        useCase.execute(userId, request.currentPassword(), request.newPassword());
        return ResponseEntity.noContent().build();
    }
}
