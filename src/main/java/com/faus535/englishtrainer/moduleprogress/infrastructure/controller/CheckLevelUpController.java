package com.faus535.englishtrainer.moduleprogress.infrastructure.controller;

import com.faus535.englishtrainer.moduleprogress.application.CheckLevelUpUseCase;
import com.faus535.englishtrainer.moduleprogress.domain.ModuleLevel;
import com.faus535.englishtrainer.moduleprogress.domain.ModuleName;
import com.faus535.englishtrainer.moduleprogress.domain.error.ModuleProgressNotFoundException;
import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
class CheckLevelUpController {

    private final CheckLevelUpUseCase useCase;

    CheckLevelUpController(CheckLevelUpUseCase useCase) {
        this.useCase = useCase;
    }

    record LevelUpResponse(boolean eligible, String currentLevel, int completedUnits, int requiredUnits) {}

    @GetMapping("/api/profiles/{userId}/modules/{module}/levels/{level}/level-up")
    @RequireProfileOwnership
    ResponseEntity<LevelUpResponse> handle(@PathVariable UUID userId,
                                           @PathVariable String module,
                                           @PathVariable String level,
                                           Authentication authentication)
            throws ModuleProgressNotFoundException {
        CheckLevelUpUseCase.LevelUpResult result = useCase.execute(
                new UserProfileId(userId),
                new ModuleName(module),
                new ModuleLevel(level)
        );
        return ResponseEntity.ok(new LevelUpResponse(
                result.eligible(),
                result.currentLevel(),
                result.completedUnits(),
                result.requiredUnits()
        ));
    }
}
