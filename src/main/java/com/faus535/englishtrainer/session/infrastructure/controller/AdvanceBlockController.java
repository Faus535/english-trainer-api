package com.faus535.englishtrainer.session.infrastructure.controller;

import com.faus535.englishtrainer.session.application.AdvanceBlockUseCase;
import com.faus535.englishtrainer.session.domain.SessionId;
import com.faus535.englishtrainer.session.domain.error.BlockNotCompletedException;
import com.faus535.englishtrainer.session.domain.error.SessionNotFoundException;
import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
class AdvanceBlockController {

    private final AdvanceBlockUseCase useCase;

    AdvanceBlockController(AdvanceBlockUseCase useCase) {
        this.useCase = useCase;
    }

    @PutMapping("/api/profiles/{profileId}/sessions/{sessionId}/blocks/{blockIndex}/advance")
    @RequireProfileOwnership(pathVariable = "profileId")
    ResponseEntity<AdvanceBlockResponse> handle(
            @PathVariable UUID profileId,
            @PathVariable UUID sessionId,
            @PathVariable int blockIndex)
            throws SessionNotFoundException, BlockNotCompletedException {

        AdvanceBlockUseCase.AdvanceBlockResult result = useCase.execute(
                new UserProfileId(profileId),
                new SessionId(sessionId),
                blockIndex
        );

        return ResponseEntity.ok(new AdvanceBlockResponse(
                result.blockIndex(),
                result.blockCompleted(),
                result.nextBlockIndex(),
                result.sessionCompleted(),
                result.completedExercises(),
                result.totalExercises()
        ));
    }
}
