package com.faus535.englishtrainer.user.infrastructure.controller;

import com.faus535.englishtrainer.user.application.MarkTestCompletedUseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
final class MarkTestCompletedController {

    private final MarkTestCompletedUseCase useCase;

    MarkTestCompletedController(MarkTestCompletedUseCase useCase) {
        this.useCase = useCase;
    }

    @PutMapping("/api/profiles/{id}/test-completed")
    ResponseEntity<Void> handle(@PathVariable UUID id) throws UserProfileNotFoundException {
        useCase.execute(new UserProfileId(id));
        return ResponseEntity.noContent().build();
    }
}
