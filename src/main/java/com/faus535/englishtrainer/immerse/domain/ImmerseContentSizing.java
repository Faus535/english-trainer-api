package com.faus535.englishtrainer.immerse.domain;

import java.util.Map;

public record ImmerseContentSizing(
        int minWords, int maxWords, int exerciseCount,
        int vocabCount, int generateMaxTokens, int processingMaxTokens
) {
    private static final Map<String, ImmerseContentSizing> SIZINGS = Map.of(
            "a1", new ImmerseContentSizing(80, 120, 3, 4, 800, 480),
            "a2", new ImmerseContentSizing(100, 180, 4, 5, 1200, 720),
            "b1", new ImmerseContentSizing(200, 350, 5, 6, 2000, 1200),
            "b2", new ImmerseContentSizing(300, 500, 5, 8, 2500, 1500),
            "c1", new ImmerseContentSizing(400, 600, 6, 10, 3000, 1800),
            "c2", new ImmerseContentSizing(500, 700, 6, 10, 3500, 2100)
    );

    public static ImmerseContentSizing forLevel(String level) {
        if (level == null) return SIZINGS.get("b1");
        ImmerseContentSizing sizing = SIZINGS.get(level.toLowerCase());
        return sizing != null ? sizing : SIZINGS.get("b1");
    }
}
