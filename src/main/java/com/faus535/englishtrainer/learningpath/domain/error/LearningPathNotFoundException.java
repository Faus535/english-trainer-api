package com.faus535.englishtrainer.learningpath.domain.error;

import com.faus535.englishtrainer.learningpath.domain.LearningPathId;
import com.faus535.englishtrainer.user.domain.UserProfileId;

public final class LearningPathNotFoundException extends LearningPathException {

    public LearningPathNotFoundException(LearningPathId id) {
        super("Learning path not found: " + id.value());
    }

    public LearningPathNotFoundException(UserProfileId userId) {
        super("Learning path not found for user: " + userId.value());
    }
}
