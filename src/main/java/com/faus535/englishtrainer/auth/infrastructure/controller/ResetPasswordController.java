package com.faus535.englishtrainer.auth.infrastructure.controller;

import com.faus535.englishtrainer.auth.application.ResetPasswordUseCase;
import com.faus535.englishtrainer.auth.domain.error.InvalidResetTokenException;
import com.faus535.englishtrainer.shared.domain.error.NotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ResetPasswordController {

    private final ResetPasswordUseCase useCase;

    ResetPasswordController(ResetPasswordUseCase useCase) {
        this.useCase = useCase;
    }

    record ResetPasswordRequest(@NotBlank String token, @NotBlank @Size(min = 6) String newPassword) {}

    @PostMapping("/api/auth/reset-password")
    ResponseEntity<Void> handle(@Valid @RequestBody ResetPasswordRequest request)
            throws InvalidResetTokenException, NotFoundException {
        useCase.execute(request.token(), request.newPassword());
        return ResponseEntity.ok().build();
    }
}
