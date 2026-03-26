package com.faus535.englishtrainer.session.domain;

import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class SessionGenerator {

    private static final String[] SECONDARY_MODULES = {"vocabulary", "grammar", "phrases", "pronunciation"};

    private static final Map<String, int[]> MODE_DURATIONS = Map.of(
            "short", new int[]{2, 5, 5, 2},
            "full", new int[]{3, 7, 7, 4},
            "extended", new int[]{4, 10, 10, 7}
    );

    private static final Map<String, Double> EXERCISES_PER_MINUTE = Map.of(
            "listening", 1.5,
            "vocabulary", 2.0,
            "grammar", 2.5,
            "pronunciation", 1.0,
            "phrases", 2.0,
            "review", 2.0,
            "mixed", 2.0
    );

    private static final int MIN_EXERCISES_PER_BLOCK = 3;

    private SessionGenerator() {
    }

    public static Session generateNormal(UserProfileId userId, SessionMode mode, int sessionCount) {
        return generateNormal(userId, mode, sessionCount, null);
    }

    public static Session generateNormal(UserProfileId userId, SessionMode mode, int sessionCount,
                                         List<ModuleWeight> weights) {
        int[] durations = MODE_DURATIONS.get(mode.value());

        if (weights != null && !weights.isEmpty()) {
            return generateWeighted(userId, mode, durations, weights);
        }

        String secondaryModule = SECONDARY_MODULES[sessionCount % SECONDARY_MODULES.length];

        List<SessionBlock> blocks = List.of(
                new SessionBlock("warmup", "review", durations[0]),
                new SessionBlock("listening", "listening", durations[1]),
                new SessionBlock("secondary", secondaryModule, durations[2]),
                new SessionBlock("practice", "mixed", durations[3])
        );

        return Session.create(userId, mode, new SessionType("normal"),
                "listening", secondaryModule, null, blocks);
    }

    private static Session generateWeighted(UserProfileId userId, SessionMode mode,
                                            int[] durations, List<ModuleWeight> weights) {
        int totalModuleTime = durations[1] + durations[2];
        List<SessionBlock> blocks = new ArrayList<>();
        blocks.add(new SessionBlock("warmup", "review", durations[0]));

        String primaryModule = weights.stream()
                .max((a, b) -> Double.compare(a.weight(), b.weight()))
                .map(ModuleWeight::moduleName)
                .orElse("listening");

        for (ModuleWeight mw : weights) {
            int blockDuration = Math.max(1, (int) Math.round(totalModuleTime * mw.weight()));
            blocks.add(new SessionBlock("weighted", mw.moduleName(), blockDuration));
        }

        blocks.add(new SessionBlock("practice", "mixed", durations[3]));

        String secondaryModule = weights.size() > 1
                ? weights.stream()
                    .filter(w -> !w.moduleName().equals(primaryModule))
                    .max((a, b) -> Double.compare(a.weight(), b.weight()))
                    .map(ModuleWeight::moduleName)
                    .orElse("mixed")
                : primaryModule;

        return Session.create(userId, mode, new SessionType("normal"),
                primaryModule, secondaryModule, null, List.copyOf(blocks));
    }

    public static Session generateIntegrator(UserProfileId userId, SessionMode mode, String theme) {
        int[] durations = MODE_DURATIONS.get(mode.value());
        int totalDuration = durations[0] + durations[1] + durations[2] + durations[3];

        List<SessionBlock> blocks = List.of(
                new SessionBlock("warmup", "review", durations[0]),
                new SessionBlock("integrator", "mixed", totalDuration - durations[0])
        );

        return Session.create(userId, mode, new SessionType("integrator"),
                "listening", "mixed", theme, blocks);
    }

    public static boolean shouldBeIntegrator(int sessionCount) {
        return sessionCount > 0 && sessionCount % 5 == 0;
    }

    public static int calculateExerciseCount(String moduleName, int durationMinutes) {
        double rate = EXERCISES_PER_MINUTE.getOrDefault(moduleName, 2.0);
        int count = (int) Math.round(rate * durationMinutes);
        return Math.max(MIN_EXERCISES_PER_BLOCK, count);
    }

    public static SessionBlock enrichBlock(SessionBlock block, List<UUID> contentIds) {
        int exerciseCount = calculateExerciseCount(block.moduleName(), block.durationMinutes());
        return new SessionBlock(block.blockType(), block.moduleName(), block.durationMinutes(),
                exerciseCount, contentIds);
    }

    public static List<SessionExercise> buildExercises(List<SessionBlock> enrichedBlocks) {
        List<SessionExercise> exercises = new ArrayList<>();
        int index = 0;
        for (SessionBlock block : enrichedBlocks) {
            for (int i = 0; i < block.exerciseCount(); i++) {
                exercises.add(new SessionExercise(
                        index++,
                        block.moduleName(),
                        block.contentIds(),
                        block.exerciseCount(),
                        null
                ));
            }
        }
        return exercises;
    }
}
