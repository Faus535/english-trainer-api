package com.faus535.englishtrainer.session.domain;

import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.util.List;
import java.util.Map;

public final class SessionGenerator {

    private static final String[] SECONDARY_MODULES = {"vocabulary", "grammar", "phrases", "pronunciation"};

    private static final Map<String, int[]> MODE_DURATIONS = Map.of(
            "short", new int[]{2, 5, 5, 2},
            "full", new int[]{3, 7, 7, 4},
            "extended", new int[]{4, 10, 10, 7}
    );

    private SessionGenerator() {
    }

    public static Session generateNormal(UserProfileId userId, SessionMode mode, int sessionCount) {
        int[] durations = MODE_DURATIONS.get(mode.value());
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
}
