package com.faus535.englishtrainer.vocabulary.domain;

public record MasterySource(String context, String detail) {

    public MasterySource {
        if (context == null || context.isBlank()) {
            throw new IllegalArgumentException("MasterySource context cannot be null or blank");
        }
    }
}
