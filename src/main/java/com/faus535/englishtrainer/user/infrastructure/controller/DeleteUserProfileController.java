package com.faus535.englishtrainer.user.infrastructure.controller;

import com.faus535.englishtrainer.user.application.DeleteUserProfileUseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
final class DeleteUserProfileController {

    private final DeleteUserProfileUseCase useCase;

    DeleteUserProfileController(DeleteUserProfileUseCase useCase) {
        this.useCase = useCase;
    }

    @DeleteMapping("/api/profiles/{id}")
    ResponseEntity<Void> handle(@PathVariable UUID id) throws UserProfileNotFoundException {
        useCase.execute(new UserProfileId(id));
        return ResponseEntity.noContent().build();
    }
}
