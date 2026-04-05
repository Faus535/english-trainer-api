package com.faus535.englishtrainer.immerse.domain.error;

import com.faus535.englishtrainer.immerse.domain.ImmerseContentId;

public final class ImmerseContentNotProcessedException extends ImmerseException {
    public ImmerseContentNotProcessedException(ImmerseContentId id) {
        super("Immerse content not yet processed: " + id.value());
    }
}
