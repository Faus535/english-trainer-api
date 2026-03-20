package com.faus535.englishtrainer.session.infrastructure.controller;

import com.faus535.englishtrainer.session.application.GenerateSessionUseCase;
import com.faus535.englishtrainer.session.domain.Session;
import com.faus535.englishtrainer.session.domain.SessionBlock;
import com.faus535.englishtrainer.session.domain.SessionMode;
import com.faus535.englishtrainer.session.domain.error.ActiveSessionExistsException;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class GenerateSessionController {

    private final GenerateSessionUseCase useCase;

    GenerateSessionController(GenerateSessionUseCase useCase) {
        this.useCase = useCase;
    }

    record GenerateSessionRequest(@NotBlank String mode) {
    }

    @PostMapping("/api/profiles/{userId}/sessions/generate")
    ResponseEntity<SessionResponse> handle(@PathVariable String userId,
                                           @Valid @RequestBody GenerateSessionRequest request)
            throws UserProfileNotFoundException, ActiveSessionExistsException {

        Session session = useCase.execute(
                UserProfileId.fromString(userId),
                new SessionMode(request.mode())
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(session));
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
