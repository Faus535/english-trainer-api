package com.faus535.englishtrainer.talk.domain;

import java.time.Instant;

public final class TalkScenarioMother {

    public static TalkScenario coffeeShop() {
        return TalkScenario.reconstitute(
                TalkScenarioId.generate(), "Coffee Shop Order",
                "Practice ordering coffee.", "You are a friendly barista.",
                "daily_life", new TalkLevel("a2"), 1, Instant.now());
    }

    public static TalkScenario jobInterview() {
        return TalkScenario.reconstitute(
                TalkScenarioId.generate(), "Job Interview",
                "Practice interview questions.", "You are a hiring manager.",
                "professional", new TalkLevel("b1"), 2, Instant.now());
    }

    public static TalkScenario withLevel(String level) {
        return TalkScenario.reconstitute(
                TalkScenarioId.generate(), "Test Scenario",
                "A test scenario.", "You are a test character.",
                "test", new TalkLevel(level), 1, Instant.now());
    }
}
