package com.faus535.englishtrainer.session.domain;

public record ModuleWeight(String moduleName, double weight) {

    public ModuleWeight {
        if (moduleName == null || moduleName.isBlank()) {
            throw new IllegalArgumentException("Module name cannot be blank");
        }
        if (weight < 0.0 || weight > 1.0) {
            throw new IllegalArgumentException("Weight must be between 0.0 and 1.0");
        }
    }
}
