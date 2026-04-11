package com.faus535.englishtrainer.talk.domain;

import java.util.Objects;

public record GrammarNote(String originalText, String correction, String explanation) {
    public GrammarNote {
        Objects.requireNonNull(originalText, "originalText must not be null");
        Objects.requireNonNull(correction, "correction must not be null");
        Objects.requireNonNull(explanation, "explanation must not be null");
    }
}
