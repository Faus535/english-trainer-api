package com.faus535.englishtrainer.auth.infrastructure.controller;

import com.faus535.englishtrainer.auth.application.DeleteAccountUseCase;
import com.faus535.englishtrainer.auth.domain.error.InvalidCredentialsException;
import com.faus535.englishtrainer.shared.domain.error.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class DeleteAccountController {

    private final DeleteAccountUseCase useCase;

    DeleteAccountController(DeleteAccountUseCase useCase) {
        this.useCase = useCase;
    }

    record DeleteAccountRequest(String password) {}

    @DeleteMapping("/api/auth/account")
    ResponseEntity<Void> handle(@RequestBody(required = false) DeleteAccountRequest request,
                                Authentication authentication)
            throws NotFoundException, InvalidCredentialsException {

        String userId = authentication.getName();
        String password = request != null ? request.password() : null;
        useCase.execute(userId, password);
        return ResponseEntity.noContent().build();
    }
}
