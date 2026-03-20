package com.faus535.englishtrainer.moduleprogress.infrastructure.controller;

import com.faus535.englishtrainer.moduleprogress.application.GetModuleProgressUseCase;
import com.faus535.englishtrainer.moduleprogress.domain.ModuleLevel;
import com.faus535.englishtrainer.moduleprogress.domain.ModuleName;
import com.faus535.englishtrainer.moduleprogress.domain.ModuleProgress;
import com.faus535.englishtrainer.moduleprogress.domain.error.ModuleProgressNotFoundException;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
class GetModuleProgressController {

    private final GetModuleProgressUseCase useCase;

    GetModuleProgressController(GetModuleProgressUseCase useCase) {
        this.useCase = useCase;
    }

    record ModuleProgressResponse(String id, String userId, String moduleName, String level,
                                  int currentUnit, List<Integer> completedUnits,
                                  Map<Integer, Integer> scores) {}

    @GetMapping("/api/profiles/{userId}/modules/{module}/levels/{level}")
    ResponseEntity<ModuleProgressResponse> handle(@PathVariable UUID userId,
                                                  @PathVariable String module,
                                                  @PathVariable String level)
            throws ModuleProgressNotFoundException {
        ModuleProgress progress = useCase.execute(
                new UserProfileId(userId),
                new ModuleName(module),
                new ModuleLevel(level)
        );
        return ResponseEntity.ok(toResponse(progress));
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
