package com.faus535.englishtrainer.pronunciation.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class PronunciationDrillMother {

    private PronunciationDrillMother() {}

    public static PronunciationDrill withThFocus() {
        return PronunciationDrill.reconstitute(
                PronunciationDrillId.generate(),
                "I thought about the theory",
                "th-sound",
                DrillDifficulty.MEDIUM,
                "b1",
                List.of()
        );
    }

    public static PronunciationDrill withAttempts(int count) {
        UUID userId = UUID.randomUUID();
        List<DrillAttempt> attempts = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            attempts.add(new DrillAttempt(UUID.randomUUID(), userId, 75, "I tought about the teory", Instant.now()));
        }
        return PronunciationDrill.reconstitute(
                PronunciationDrillId.generate(),
                "I thought about the theory",
                "th-sound",
                DrillDifficulty.MEDIUM,
                "b1",
                attempts
        );
    }

    public static PronunciationDrill withUserPerfectStreak(UUID userId, int streak) {
        List<DrillAttempt> attempts = new ArrayList<>();
        for (int i = 0; i < streak; i++) {
            attempts.add(new DrillAttempt(UUID.randomUUID(), userId, 95, "I thought about the theory", Instant.now()));
        }
        return PronunciationDrill.reconstitute(
                PronunciationDrillId.generate(),
                "I thought about the theory",
                "th-sound",
                DrillDifficulty.MEDIUM,
                "b1",
                attempts
        );
    }

    public static PronunciationDrill withFocusAndLevel(String focus, String cefrLevel) {
        return PronunciationDrill.reconstitute(
                PronunciationDrillId.generate(),
                "She sells seashells",
                focus,
                DrillDifficulty.MEDIUM,
                cefrLevel,
                List.of()
        );
    }
}
