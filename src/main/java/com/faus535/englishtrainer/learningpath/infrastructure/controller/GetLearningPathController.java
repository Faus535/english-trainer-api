package com.faus535.englishtrainer.learningpath.infrastructure.controller;

import com.faus535.englishtrainer.learningpath.application.GetLearningPathUseCase;
import com.faus535.englishtrainer.learningpath.application.GetLearningPathUseCase.LearningPathSummary;
import com.faus535.englishtrainer.learningpath.domain.error.LearningPathNotFoundException;
import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
class GetLearningPathController {

    private final GetLearningPathUseCase useCase;

    GetLearningPathController(GetLearningPathUseCase useCase) {
        this.useCase = useCase;
    }

    record UnitInfoResponse(int unitIndex, String name, String status, int masteryScore) {}

    record LearningPathResponse(String currentLevel, int currentUnitIndex,
                                 List<UnitInfoResponse> units) {}

    @GetMapping("/api/profiles/{profileId}/learning-path")
    @RequireProfileOwnership(pathVariable = "profileId")
    ResponseEntity<LearningPathResponse> handle(@PathVariable UUID profileId)
            throws LearningPathNotFoundException {
        LearningPathSummary summary = useCase.execute(new UserProfileId(profileId));
        return ResponseEntity.ok(toResponse(summary));
    }

    private LearningPathResponse toResponse(LearningPathSummary summary) {
        List<UnitInfoResponse> units = summary.units().stream()
                .map(u -> new UnitInfoResponse(u.unitIndex(), u.name(), u.status(), u.masteryScore()))
                .toList();

        return new LearningPathResponse(summary.currentLevel(), summary.currentUnitIndex(), units);
    }
}
