package com.faus535.englishtrainer.learningpath.domain.error;

import com.faus535.englishtrainer.learningpath.domain.LearningUnitId;

public final class LearningUnitNotFoundException extends LearningPathException {

    public LearningUnitNotFoundException(LearningUnitId id) {
        super("Learning unit not found: " + id.value());
    }
}
