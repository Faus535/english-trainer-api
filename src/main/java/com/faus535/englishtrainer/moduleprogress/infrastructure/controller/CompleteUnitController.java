package com.faus535.englishtrainer.moduleprogress.infrastructure.controller;

import com.faus535.englishtrainer.moduleprogress.application.CompleteUnitUseCase;
import com.faus535.englishtrainer.moduleprogress.domain.ModuleLevel;
import com.faus535.englishtrainer.moduleprogress.domain.ModuleName;
import com.faus535.englishtrainer.moduleprogress.domain.ModuleProgress;
import com.faus535.englishtrainer.moduleprogress.domain.error.ModuleProgressNotFoundException;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
class CompleteUnitController {

    private final CompleteUnitUseCase useCase;

    CompleteUnitController(CompleteUnitUseCase useCase) {
        this.useCase = useCase;
    }

    record CompleteUnitRequest(@NotNull @Min(0) @Max(100) Integer score) {}

    record ModuleProgressResponse(String id, String userId, String moduleName, String level,
                                  int currentUnit, List<Integer> completedUnits,
                                  Map<Integer, Integer> scores) {}

    @PutMapping("/api/profiles/{userId}/modules/{module}/levels/{level}/units/{unit}")
    ResponseEntity<ModuleProgressResponse> handle(@PathVariable UUID userId,
                                                  @PathVariable String module,
                                                  @PathVariable String level,
                                                  @PathVariable int unit,
                                                  @Valid @RequestBody CompleteUnitRequest request)
            throws ModuleProgressNotFoundException {
        ModuleProgress progress = useCase.execute(
                new UserProfileId(userId),
                new ModuleName(module),
                new ModuleLevel(level),
                unit,
                request.score()
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
