package com.faus535.englishtrainer.session.infrastructure.controller;

import com.faus535.englishtrainer.session.application.GetBlockExercisesUseCase;
import com.faus535.englishtrainer.session.domain.SessionExercise;
import com.faus535.englishtrainer.session.domain.SessionId;
import com.faus535.englishtrainer.session.domain.error.SessionNotFoundException;
import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
class GetBlockExercisesController {

    private final GetBlockExercisesUseCase useCase;

    GetBlockExercisesController(GetBlockExercisesUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/api/profiles/{profileId}/sessions/{sessionId}/blocks/{blockIndex}/exercises")
    @RequireProfileOwnership(pathVariable = "profileId")
    ResponseEntity<List<SessionExerciseResponse>> handle(
            @PathVariable UUID profileId,
            @PathVariable UUID sessionId,
            @PathVariable int blockIndex)
            throws SessionNotFoundException {

        List<SessionExercise> exercises = useCase.execute(
                new UserProfileId(profileId),
                new SessionId(sessionId),
                blockIndex
        );

        List<SessionExerciseResponse> response = exercises.stream()
                .map(ex -> new SessionExerciseResponse(
                        ex.exerciseIndex(),
                        ex.blockIndex(),
                        ex.exerciseType(),
                        ex.targetCount(),
                        ex.isCompleted(),
                        ex.result() != null ? ex.result().correctCount() : null,
                        ex.result() != null ? ex.result().totalCount() : null
                ))
                .toList();

        return ResponseEntity.ok(response);
    }
}
