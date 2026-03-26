package com.faus535.englishtrainer.learningpath.domain;

import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.util.Optional;

public interface LearningPathRepository {

    Optional<LearningPath> findById(LearningPathId id);

    Optional<LearningPath> findByUserId(UserProfileId userId);

    LearningPath save(LearningPath learningPath);

    void deleteByUserId(UserProfileId userId);
}
