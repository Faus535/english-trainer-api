package com.faus535.englishtrainer.learningpath.infrastructure.controller;

import com.faus535.englishtrainer.learningpath.application.GetLearningStatusUseCase;
import com.faus535.englishtrainer.learningpath.application.GetLearningStatusUseCase.LearningStatus;
import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
class GetLearningStatusController {

    private final GetLearningStatusUseCase useCase;

    GetLearningStatusController(GetLearningStatusUseCase useCase) {
        this.useCase = useCase;
    }

    record ContentProgressResponse(int practiced, int total) {}

    record UnitSummaryResponse(String name, int unitIndex, int masteryScore, String status,
                                ContentProgressResponse contentProgress) {}

    record NextUnitResponse(String name, int unitIndex) {}

    record OverallProgressResponse(int unitsCompleted, int totalUnits, int percentComplete) {}

    record TodaysPlanResponse(int newItemsCount, int reviewItemsCount, int estimatedMinutes,
                               String suggestedSessionMode) {}

    record WeakAreaResponse(String module, String unitName, int masteryScore) {}

    record MilestoneResponse(String type, String description, String date) {}

    record LearningStatusResponse(
            UnitSummaryResponse currentUnit,
            NextUnitResponse nextUnit,
            OverallProgressResponse overallProgress,
            TodaysPlanResponse todaysPlan,
            List<WeakAreaResponse> weakAreas,
            int currentStreak,
            List<MilestoneResponse> milestones
    ) {}

    @GetMapping("/api/profiles/{profileId}/learning-status")
    @RequireProfileOwnership(pathVariable = "profileId")
    ResponseEntity<LearningStatusResponse> handle(@PathVariable UUID profileId) {
        LearningStatus status = useCase.execute(new UserProfileId(profileId));
        return ResponseEntity.ok(toResponse(status));
    }

    private LearningStatusResponse toResponse(LearningStatus status) {
        UnitSummaryResponse currentUnit = status.currentUnit() != null
                ? new UnitSummaryResponse(
                        status.currentUnit().name(),
                        status.currentUnit().unitIndex(),
                        status.currentUnit().masteryScore(),
                        status.currentUnit().status(),
                        new ContentProgressResponse(
                                status.currentUnit().contentProgress().practiced(),
                                status.currentUnit().contentProgress().total()
                        ))
                : null;

        NextUnitResponse nextUnit = status.nextUnit() != null
                ? new NextUnitResponse(status.nextUnit().name(), status.nextUnit().unitIndex())
                : null;

        OverallProgressResponse overallProgress = new OverallProgressResponse(
                status.overallProgress().unitsCompleted(),
                status.overallProgress().totalUnits(),
                status.overallProgress().percentComplete()
        );

        TodaysPlanResponse todaysPlan = new TodaysPlanResponse(
                status.todaysPlan().newItemsCount(),
                status.todaysPlan().reviewItemsCount(),
                status.todaysPlan().estimatedMinutes(),
                status.todaysPlan().suggestedSessionMode()
        );

        List<WeakAreaResponse> weakAreas = status.weakAreas().stream()
                .map(w -> new WeakAreaResponse(w.module(), w.unitName(), w.masteryScore()))
                .toList();

        List<MilestoneResponse> milestones = status.milestones().stream()
                .map(m -> new MilestoneResponse(m.type(), m.description(), m.date()))
                .toList();

        return new LearningStatusResponse(
                currentUnit, nextUnit, overallProgress, todaysPlan,
                weakAreas, status.currentStreak(), milestones
        );
    }
}
