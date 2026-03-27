package com.faus535.englishtrainer.session.infrastructure.controller;

import com.faus535.englishtrainer.session.application.CompleteSessionUseCase;
import com.faus535.englishtrainer.session.domain.Session;
import com.faus535.englishtrainer.session.domain.SessionId;
import com.faus535.englishtrainer.session.domain.error.IncompleteSessionException;
import com.faus535.englishtrainer.session.domain.error.SessionNotFoundException;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class CompleteSessionController {

    private final CompleteSessionUseCase useCase;

    CompleteSessionController(CompleteSessionUseCase useCase) {
        this.useCase = useCase;
    }

    record CompleteSessionRequest(@NotNull @Min(1) Integer durationMinutes) {
    }

    @PutMapping("/api/profiles/{userId}/sessions/{sessionId}/complete")
    @RequireProfileOwnership
    ResponseEntity<SessionResponse> handle(@PathVariable String userId,
                                           @PathVariable String sessionId,
                                           @Valid @RequestBody CompleteSessionRequest request,
                                           Authentication authentication)
            throws SessionNotFoundException, UserProfileNotFoundException, IncompleteSessionException {

        Session session = useCase.execute(
                SessionId.fromString(sessionId),
                request.durationMinutes()
        );

        return ResponseEntity.ok(SessionResponseMapper.toResponse(session));
    }
}
