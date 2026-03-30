package com.faus535.englishtrainer.learningpath.application;

import com.faus535.englishtrainer.activity.domain.ActivityDate;
import com.faus535.englishtrainer.activity.domain.ActivityDateRepository;
import com.faus535.englishtrainer.activity.domain.StreakCalculator;
import com.faus535.englishtrainer.activity.domain.StreakInfo;
import com.faus535.englishtrainer.learningpath.domain.LearningPath;
import com.faus535.englishtrainer.learningpath.domain.LearningPathRepository;
import com.faus535.englishtrainer.learningpath.domain.LearningUnit;
import com.faus535.englishtrainer.learningpath.domain.LearningUnitRepository;
import com.faus535.englishtrainer.learningpath.domain.UnitContent;
import com.faus535.englishtrainer.learningpath.domain.UnitStatus;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@UseCase
public class GetLearningStatusUseCase {

    private static final double MINUTES_PER_ITEM = 0.5;

    private final LearningPathRepository learningPathRepository;
    private final LearningUnitRepository learningUnitRepository;
    private final SpacedRepetitionRepository spacedRepetitionRepository;
    private final ActivityDateRepository activityDateRepository;

    public GetLearningStatusUseCase(LearningPathRepository learningPathRepository,
                                     LearningUnitRepository learningUnitRepository,
                                     SpacedRepetitionRepository spacedRepetitionRepository,
                                     ActivityDateRepository activityDateRepository) {
        this.learningPathRepository = learningPathRepository;
        this.learningUnitRepository = learningUnitRepository;
        this.spacedRepetitionRepository = spacedRepetitionRepository;
        this.activityDateRepository = activityDateRepository;
    }

    public record ContentProgress(int practiced, int total) {}

    public record UnitSummary(String name, int unitIndex, int masteryScore, String status,
                               ContentProgress contentProgress) {}

    public record NextUnit(String name, int unitIndex) {}

    public record OverallProgress(int unitsCompleted, int totalUnits, int percentComplete) {}

    public record TodaysPlan(int newItemsCount, int reviewItemsCount, int estimatedMinutes,
                              String suggestedSessionMode) {}

    public record WeakArea(String module, String unitName, int masteryScore) {}

    public record Milestone(String type, String description, String date) {}

    public record LearningStatus(
            UnitSummary currentUnit,
            NextUnit nextUnit,
            OverallProgress overallProgress,
            TodaysPlan todaysPlan,
            List<WeakArea> weakAreas,
            int currentStreak,
            List<Milestone> milestones
    ) {}

    @Transactional(readOnly = true)
    public LearningStatus execute(UserProfileId userId) {
        var optionalPath = learningPathRepository.findByUserId(userId);

        if (optionalPath.isEmpty()) {
            int currentStreak = calculateStreak(userId);
            return new LearningStatus(
                    null,
                    null,
                    new OverallProgress(0, 0, 0),
                    new TodaysPlan(0, 0, 0, "not_started"),
                    List.of(),
                    currentStreak,
                    List.of()
            );
        }

        LearningPath path = optionalPath.get();
        List<LearningUnit> allUnits = learningUnitRepository.findByLearningPathId(path.id());

        UnitSummary currentUnit = buildCurrentUnitSummary(path, allUnits);
        NextUnit nextUnit = buildNextUnit(path, allUnits);
        OverallProgress overallProgress = buildOverallProgress(allUnits);
        TodaysPlan todaysPlan = buildTodaysPlan(userId, path, allUnits);
        List<WeakArea> weakAreas = buildWeakAreas(allUnits);
        int currentStreak = calculateStreak(userId);

        return new LearningStatus(
                currentUnit,
                nextUnit,
                overallProgress,
                todaysPlan,
                weakAreas,
                currentStreak,
                List.of()
        );
    }

    private UnitSummary buildCurrentUnitSummary(LearningPath path, List<LearningUnit> allUnits) {
        if (path.isCompleted()) {
            LearningUnit lastUnit = allUnits.get(allUnits.size() - 1);
            return toUnitSummary(lastUnit);
        }

        return allUnits.stream()
                .filter(u -> u.unitIndex() == path.currentUnitIndex())
                .findFirst()
                .map(this::toUnitSummary)
                .orElse(null);
    }

    private UnitSummary toUnitSummary(LearningUnit unit) {
        int practiced = (int) unit.contents().stream().filter(UnitContent::practiced).count();
        int total = unit.contents().size();
        return new UnitSummary(
                unit.unitName(),
                unit.unitIndex(),
                unit.masteryScore().value(),
                unit.status().name(),
                new ContentProgress(practiced, total)
        );
    }

    private NextUnit buildNextUnit(LearningPath path, List<LearningUnit> allUnits) {
        int nextIndex = path.currentUnitIndex() + 1;
        if (nextIndex >= allUnits.size()) {
            return null;
        }

        return allUnits.stream()
                .filter(u -> u.unitIndex() == nextIndex)
                .findFirst()
                .map(u -> new NextUnit(u.unitName(), u.unitIndex()))
                .orElse(null);
    }

    private OverallProgress buildOverallProgress(List<LearningUnit> allUnits) {
        int totalUnits = allUnits.size();
        int completed = (int) allUnits.stream()
                .filter(u -> u.status() == UnitStatus.MASTERED)
                .count();
        int percentComplete = totalUnits > 0 ? (completed * 100) / totalUnits : 0;
        return new OverallProgress(completed, totalUnits, percentComplete);
    }

    private TodaysPlan buildTodaysPlan(UserProfileId userId, LearningPath path,
                                        List<LearningUnit> allUnits) {
        int newItemsCount = 0;
        if (!path.isCompleted()) {
            newItemsCount = allUnits.stream()
                    .filter(u -> u.unitIndex() == path.currentUnitIndex())
                    .findFirst()
                    .map(u -> u.unpracticedContents().size())
                    .orElse(0);
        }

        int reviewItemsCount = spacedRepetitionRepository
                .findDueByUser(userId, LocalDate.now())
                .size();

        int totalItems = newItemsCount + reviewItemsCount;
        int estimatedMinutes = (int) Math.ceil(totalItems * MINUTES_PER_ITEM);

        String suggestedSessionMode;
        if (reviewItemsCount > newItemsCount) {
            suggestedSessionMode = "review";
        } else if (newItemsCount > 0) {
            suggestedSessionMode = "learn";
        } else {
            suggestedSessionMode = "practice";
        }

        return new TodaysPlan(newItemsCount, reviewItemsCount, estimatedMinutes, suggestedSessionMode);
    }

    private List<WeakArea> buildWeakAreas(List<LearningUnit> allUnits) {
        return allUnits.stream()
                .filter(u -> u.status() == UnitStatus.NEEDS_REVIEW)
                .sorted(Comparator.comparingInt(u -> u.masteryScore().value()))
                .map(u -> new WeakArea(u.targetLevel(), u.unitName(), u.masteryScore().value()))
                .toList();
    }

    private int calculateStreak(UserProfileId userId) {
        List<LocalDate> sortedDatesDescending = activityDateRepository.findAllByUser(userId).stream()
                .map(ActivityDate::activityDate)
                .sorted(Comparator.reverseOrder())
                .toList();

        StreakInfo streakInfo = StreakCalculator.calculate(sortedDatesDescending);
        return streakInfo.currentStreak();
    }
}
