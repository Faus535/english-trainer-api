package com.faus535.englishtrainer.learningpath.infrastructure.controller;

import com.faus535.englishtrainer.learningpath.application.GenerateLearningPathUseCase;
import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
class GenerateLearningPathController {

    private final GenerateLearningPathUseCase useCase;

    GenerateLearningPathController(GenerateLearningPathUseCase useCase) {
        this.useCase = useCase;
    }

    record GenerateLearningPathResponse(String pathId, String level, int unitCount, long totalContentItems) {}

    @PostMapping("/api/profiles/{profileId}/learning-path/generate")
    @RequireProfileOwnership(pathVariable = "profileId")
    ResponseEntity<GenerateLearningPathResponse> handle(@PathVariable UUID profileId)
            throws UserProfileNotFoundException {
        var summary = useCase.execute(new UserProfileId(profileId));
        return ResponseEntity.status(HttpStatus.CREATED).body(new GenerateLearningPathResponse(
                summary.pathId().toString(),
                summary.level(),
                summary.unitCount(),
                summary.totalContentItems()
        ));
    }
}
