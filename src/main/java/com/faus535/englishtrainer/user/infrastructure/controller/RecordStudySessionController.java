package com.faus535.englishtrainer.user.infrastructure.controller;

import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import com.faus535.englishtrainer.user.application.RecordStudySessionUseCase;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import com.faus535.englishtrainer.user.domain.vo.StudyModule;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
class RecordStudySessionController {

    private final RecordStudySessionUseCase useCase;

    RecordStudySessionController(RecordStudySessionUseCase useCase) {
        this.useCase = useCase;
    }

    record RecordStudySessionRequest(
            @NotNull StudyModule module,
            @NotNull @Min(1) Integer durationSeconds
    ) {}

    @PostMapping("/api/profiles/{id}/sessions")
    @RequireProfileOwnership(pathVariable = "id")
    ResponseEntity<Void> handle(@PathVariable UUID id,
                                @Valid @RequestBody RecordStudySessionRequest request,
                                Authentication authentication) throws UserProfileNotFoundException {
        useCase.execute(id, request.module(), request.durationSeconds());
        return ResponseEntity.status(201).build();
    }
}
