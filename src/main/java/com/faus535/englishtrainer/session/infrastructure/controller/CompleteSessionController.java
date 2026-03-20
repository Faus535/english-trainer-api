package com.faus535.englishtrainer.session.infrastructure.controller;

import com.faus535.englishtrainer.session.application.CompleteSessionUseCase;
import com.faus535.englishtrainer.session.domain.Session;
import com.faus535.englishtrainer.session.domain.SessionId;
import com.faus535.englishtrainer.session.domain.error.SessionNotFoundException;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
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
    ResponseEntity<SessionResponse> handle(@PathVariable String userId,
                                           @PathVariable String sessionId,
                                           @Valid @RequestBody CompleteSessionRequest request)
            throws SessionNotFoundException, UserProfileNotFoundException {

        Session session = useCase.execute(
                SessionId.fromString(sessionId),
                request.durationMinutes()
        );

        return ResponseEntity.ok(toResponse(session));
    }

    private SessionResponse toResponse(Session session) {
        return new SessionResponse(
                session.id().value().toString(),
                session.userId().value().toString(),
                session.mode().value(),
                session.sessionType().value(),
                session.listeningModule(),
                session.secondaryModule(),
                session.integratorTheme(),
                session.blocks().stream()
                        .map(b -> new SessionBlockResponse(b.blockType(), b.moduleName(), b.durationMinutes()))
                        .toList(),
                session.completed(),
                session.startedAt().toString(),
                session.completedAt() != null ? session.completedAt().toString() : null,
                session.durationMinutes()
        );
    }
}
