package com.faus535.englishtrainer.moduleprogress.domain;

import java.util.UUID;

public record ModuleProgressId(UUID value) {

    public ModuleProgressId {
        if (value == null) {
            throw new IllegalArgumentException("ModuleProgressId cannot be null");
        }
    }

    public static ModuleProgressId generate() {
        return new ModuleProgressId(UUID.randomUUID());
    }

    public static ModuleProgressId fromString(String id) {
        return new ModuleProgressId(UUID.fromString(id));
    }
}
