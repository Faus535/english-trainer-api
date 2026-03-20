package com.faus535.englishtrainer.moduleprogress.domain;

import com.faus535.englishtrainer.shared.domain.AggregateRoot;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ModuleProgress extends AggregateRoot<ModuleProgressId> {

    private final ModuleProgressId id;
    private final UserProfileId userId;
    private final ModuleName moduleName;
    private final ModuleLevel level;
    private final int currentUnit;
    private final List<Integer> completedUnits;
    private final Map<Integer, Integer> scores;

    private ModuleProgress(ModuleProgressId id, UserProfileId userId, ModuleName moduleName,
                           ModuleLevel level, int currentUnit, List<Integer> completedUnits,
                           Map<Integer, Integer> scores) {
        this.id = id;
        this.userId = userId;
        this.moduleName = moduleName;
        this.level = level;
        this.currentUnit = currentUnit;
        this.completedUnits = Collections.unmodifiableList(new ArrayList<>(completedUnits));
        this.scores = Collections.unmodifiableMap(new HashMap<>(scores));
    }

    public static ModuleProgress create(UserProfileId userId, ModuleName moduleName, ModuleLevel level) {
        return new ModuleProgress(
                ModuleProgressId.generate(),
                userId,
                moduleName,
                level,
                0,
                List.of(),
                Map.of()
        );
    }

    public static ModuleProgress reconstitute(ModuleProgressId id, UserProfileId userId, ModuleName moduleName,
                                              ModuleLevel level, int currentUnit, List<Integer> completedUnits,
                                              Map<Integer, Integer> scores) {
        return new ModuleProgress(id, userId, moduleName, level, currentUnit, completedUnits, scores);
    }

    public ModuleProgress completeUnit(int unitIndex, int score) {
        List<Integer> newCompletedUnits = new ArrayList<>(completedUnits);
        if (!newCompletedUnits.contains(unitIndex)) {
            newCompletedUnits.add(unitIndex);
        }

        Map<Integer, Integer> newScores = new HashMap<>(scores);
        newScores.put(unitIndex, score);

        int newCurrentUnit = Math.max(currentUnit, unitIndex + 1);

        return new ModuleProgress(id, userId, moduleName, level, newCurrentUnit, newCompletedUnits, newScores);
    }

    public boolean isLevelComplete(int totalUnitsInLevel) {
        return completedUnits.size() >= totalUnitsInLevel;
    }

    public int averageScore() {
        if (scores.isEmpty()) {
            return 0;
        }
        int sum = scores.values().stream().mapToInt(Integer::intValue).sum();
        return sum / scores.size();
    }

    public ModuleProgressId id() { return id; }
    public UserProfileId userId() { return userId; }
    public ModuleName moduleName() { return moduleName; }
    public ModuleLevel level() { return level; }
    public int currentUnit() { return currentUnit; }
    public List<Integer> completedUnits() { return completedUnits; }
    public Map<Integer, Integer> scores() { return scores; }
}
