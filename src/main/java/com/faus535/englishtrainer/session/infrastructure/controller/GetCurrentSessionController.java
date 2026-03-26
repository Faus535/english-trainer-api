package com.faus535.englishtrainer.session.infrastructure.controller;

import com.faus535.englishtrainer.session.application.GetCurrentSessionUseCase;
import com.faus535.englishtrainer.session.domain.BlockProgress;
import com.faus535.englishtrainer.session.domain.Session;
import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
class GetCurrentSessionController {

    private final GetCurrentSessionUseCase useCase;

    GetCurrentSessionController(GetCurrentSessionUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/api/profiles/{userId}/sessions/current")
    @RequireProfileOwnership
    ResponseEntity<SessionResponse> handle(@PathVariable String userId,
                                            Authentication authentication) {
        Optional<Session> session = useCase.execute(UserProfileId.fromString(userId));

        return session
                .map(s -> ResponseEntity.ok(toResponse(s)))
                .orElseGet(() -> ResponseEntity.notFound().build());
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
                        .map((b) -> {
                            int blockIdx = session.blocks().indexOf(b);
                            BlockProgress progress = session.getBlockProgress(blockIdx);
                            return new SessionBlockResponse(
                                    b.blockType(), b.moduleName(), b.durationMinutes(),
                                    b.exerciseCount(), progress.completedExercises(), progress.isCompleted());
                        })
                        .toList(),
                session.exercises().stream()
                        .map(ex -> new SessionExerciseResponse(
                                ex.exerciseIndex(), ex.blockIndex(), ex.exerciseType(),
                                ex.targetCount(), ex.isCompleted(),
                                ex.result() != null ? ex.result().correctCount() : null,
                                ex.result() != null ? ex.result().totalCount() : null))
                        .toList(),
                session.completed(),
                session.startedAt().toString(),
                session.completedAt() != null ? session.completedAt().toString() : null,
                session.durationMinutes()
        );
    }
}
