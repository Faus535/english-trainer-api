package com.faus535.englishtrainer.session.infrastructure.controller;

import com.faus535.englishtrainer.learningpath.application.RecordExerciseResultUseCase;
import com.faus535.englishtrainer.learningpath.domain.error.LearningPathNotFoundException;
import com.faus535.englishtrainer.learningpath.domain.error.LearningUnitNotFoundException;
import com.faus535.englishtrainer.session.domain.ExerciseResult;
import com.faus535.englishtrainer.session.domain.error.SessionNotFoundException;
import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
class RecordExerciseResultController {

    private final RecordExerciseResultUseCase useCase;

    RecordExerciseResultController(RecordExerciseResultUseCase useCase) {
        this.useCase = useCase;
    }

    record RecordExerciseResultRequest(
            @NotNull @Min(0) Integer correctCount,
            @NotNull @Min(0) Integer totalCount,
            @Min(0) Long averageResponseTimeMs,
            @NotBlank String exerciseType) {}

    record RecordExerciseResultResponse(
            int unitMasteryScore,
            String unitStatus,
            int xpEarned,
            int blockIndex,
            boolean blockCompleted,
            int completedExercisesInBlock,
            int totalExercisesInBlock
    ) {}

    @PostMapping("/api/profiles/{profileId}/sessions/{sessionId}/exercises/{exerciseIndex}/result")
    @RequireProfileOwnership(pathVariable = "profileId")
    ResponseEntity<RecordExerciseResultResponse> handle(
            @PathVariable UUID profileId,
            @PathVariable UUID sessionId,
            @PathVariable int exerciseIndex,
            @Valid @RequestBody RecordExerciseResultRequest request)
            throws SessionNotFoundException, LearningPathNotFoundException, LearningUnitNotFoundException {

        UserProfileId userId = new UserProfileId(profileId);

        ExerciseResult result = new ExerciseResult(
                request.correctCount(),
                request.totalCount(),
                request.averageResponseTimeMs() != null ? request.averageResponseTimeMs() : 0L,
                Instant.now()
        );

        RecordExerciseResultUseCase.RecordResult recordResult =
                useCase.execute(userId, sessionId, exerciseIndex, result);

        return ResponseEntity.ok(new RecordExerciseResultResponse(
                recordResult.unitMasteryScore(),
                recordResult.unitStatus(),
                recordResult.xpEarned(),
                recordResult.blockIndex(),
                recordResult.blockCompleted(),
                recordResult.completedExercisesInBlock(),
                recordResult.totalExercisesInBlock()
        ));
    }
}
