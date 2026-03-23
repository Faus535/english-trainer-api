package com.faus535.englishtrainer.errorpattern.domain;

import java.util.UUID;

public record ErrorPatternId(UUID value) {

    public static ErrorPatternId generate() {
        return new ErrorPatternId(UUID.randomUUID());
    }
}
