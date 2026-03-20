package com.faus535.englishtrainer.assessment.domain;

import java.util.UUID;

public record LevelTestResultId(UUID value) {

    public LevelTestResultId {
        if (value == null) {
            throw new IllegalArgumentException("LevelTestResultId cannot be null");
        }
    }

    public static LevelTestResultId generate() {
        return new LevelTestResultId(UUID.randomUUID());
    }

    public static LevelTestResultId fromString(String id) {
        return new LevelTestResultId(UUID.fromString(id));
    }
}
