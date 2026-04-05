package com.faus535.englishtrainer.immerse.domain.error;

import com.faus535.englishtrainer.immerse.domain.ImmerseContentId;

public final class ImmerseContentNotFoundException extends ImmerseException {
    public ImmerseContentNotFoundException(ImmerseContentId id) {
        super("Immerse content not found: " + id.value());
    }
}
