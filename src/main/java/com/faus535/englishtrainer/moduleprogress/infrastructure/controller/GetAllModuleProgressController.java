package com.faus535.englishtrainer.moduleprogress.infrastructure.controller;

import com.faus535.englishtrainer.moduleprogress.application.GetAllModuleProgressUseCase;
import com.faus535.englishtrainer.moduleprogress.domain.ModuleProgress;
import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
class GetAllModuleProgressController {

    private final GetAllModuleProgressUseCase useCase;

    GetAllModuleProgressController(GetAllModuleProgressUseCase useCase) {
        this.useCase = useCase;
    }

    record ModuleProgressResponse(String id, String userId, String moduleName, String level,
                                  int currentUnit, List<Integer> completedUnits,
                                  Map<Integer, Integer> scores) {}

    @GetMapping("/api/profiles/{userId}/modules")
    @RequireProfileOwnership
    ResponseEntity<List<ModuleProgressResponse>> handle(@PathVariable UUID userId,
                                                         Authentication authentication) {
        List<ModuleProgress> progressList = useCase.execute(new UserProfileId(userId));
        List<ModuleProgressResponse> response = progressList.stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(response);
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
