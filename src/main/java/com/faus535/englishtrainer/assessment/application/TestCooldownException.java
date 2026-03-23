package com.faus535.englishtrainer.assessment.application;

import java.time.Instant;

public class TestCooldownException extends Exception {

    private final Instant lastTestAt;

    public TestCooldownException(Instant lastTestAt) {
        super("Test cooldown active. Last test at: " + lastTestAt);
        this.lastTestAt = lastTestAt;
    }

    public Instant lastTestAt() { return lastTestAt; }

    public Instant availableAt() {
        return lastTestAt.plusSeconds(24 * 60 * 60);
    }
}
