package com.faus535.englishtrainer.talk.domain;

import java.util.UUID;

public record TalkScenarioId(UUID value) {

    public TalkScenarioId {
        if (value == null) {
            throw new IllegalArgumentException("TalkScenarioId cannot be null");
        }
    }

    public static TalkScenarioId generate() {
        return new TalkScenarioId(UUID.randomUUID());
    }
}
