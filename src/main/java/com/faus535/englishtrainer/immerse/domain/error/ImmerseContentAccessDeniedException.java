package com.faus535.englishtrainer.immerse.domain.error;

import com.faus535.englishtrainer.immerse.domain.ImmerseContentId;

public class ImmerseContentAccessDeniedException extends ImmerseException {
    public ImmerseContentAccessDeniedException(ImmerseContentId id) {
        super("Access denied to immerse content: " + id.value());
    }
}
