package com.faus535.englishtrainer.talk.domain;

import java.util.Objects;

public record VocabItem(String word, String definition, String usedInContext) {
    public VocabItem {
        Objects.requireNonNull(word, "word must not be null");
        Objects.requireNonNull(definition, "definition must not be null");
        Objects.requireNonNull(usedInContext, "usedInContext must not be null");
    }
}
