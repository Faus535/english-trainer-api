package com.faus535.englishtrainer.moduleprogress.infrastructure.controller;

import com.faus535.englishtrainer.moduleprogress.application.InitModuleProgressUseCase;
import com.faus535.englishtrainer.moduleprogress.domain.ModuleLevel;
import com.faus535.englishtrainer.moduleprogress.domain.ModuleName;
import com.faus535.englishtrainer.moduleprogress.domain.ModuleProgress;
import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
class InitModuleProgressController {

    private final InitModuleProgressUseCase useCase;

    InitModuleProgressController(InitModuleProgressUseCase useCase) {
        this.useCase = useCase;
    }

    record ModuleProgressResponse(String id, String userId, String moduleName, String level,
                                  int currentUnit, List<Integer> completedUnits,
                                  Map<Integer, Integer> scores) {}

    @PostMapping("/api/profiles/{userId}/modules/{module}/levels/{level}")
    @RequireProfileOwnership
    ResponseEntity<ModuleProgressResponse> handle(@PathVariable UUID userId,
                                                  @PathVariable String module,
                                                  @PathVariable String level,
                                                  Authentication authentication) {
        ModuleProgress progress = useCase.execute(
                new UserProfileId(userId),
                new ModuleName(module),
                new ModuleLevel(level)
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(progress));
    }

    private ModuleProgressResponse toResponse(ModuleProgress progress) {
        return new ModuleProgressResponse(
                progress.id().value().toString(),
                progress.userId().value().toString(),
                progress.moduleName().value(),
                progress.level().value(),
                progress.currentUnit(),
                progress.completedUnits(),
                progress.scores()
        );
    }
}
