package com.faus535.englishtrainer.assessment.domain;

import java.util.UUID;

public record MiniTestResultId(UUID value) {

    public MiniTestResultId {
        if (value == null) {
            throw new IllegalArgumentException("MiniTestResultId cannot be null");
        }
    }

    public static MiniTestResultId generate() {
        return new MiniTestResultId(UUID.randomUUID());
    }

    public static MiniTestResultId fromString(String id) {
        return new MiniTestResultId(UUID.fromString(id));
    }
}
