package com.faus535.englishtrainer.session.infrastructure.controller;

import com.faus535.englishtrainer.session.application.GenerateSessionUseCase;
import com.faus535.englishtrainer.session.domain.BlockProgress;
import com.faus535.englishtrainer.session.domain.ModuleWeight;
import com.faus535.englishtrainer.session.domain.Session;
import com.faus535.englishtrainer.session.domain.SessionMode;
import com.faus535.englishtrainer.session.domain.error.ActiveSessionExistsException;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class GenerateSessionController {

    private final GenerateSessionUseCase useCase;

    GenerateSessionController(GenerateSessionUseCase useCase) {
        this.useCase = useCase;
    }

    record WeightRequest(String moduleName, double weight) {}

    record GenerateSessionRequest(@NotBlank String mode, List<WeightRequest> weights) {
    }

    @PostMapping("/api/profiles/{userId}/sessions/generate")
    @RequireProfileOwnership
    ResponseEntity<SessionResponse> handle(@PathVariable String userId,
                                           @Valid @RequestBody GenerateSessionRequest request,
                                           Authentication authentication)
            throws UserProfileNotFoundException, ActiveSessionExistsException {

        List<ModuleWeight> weights = request.weights() != null
                ? request.weights().stream()
                    .map(w -> new ModuleWeight(w.moduleName(), w.weight()))
                    .toList()
                : null;

        Session session = useCase.execute(
                UserProfileId.fromString(userId),
                new SessionMode(request.mode()),
                weights
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
