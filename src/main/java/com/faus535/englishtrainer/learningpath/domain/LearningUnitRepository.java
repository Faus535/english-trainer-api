package com.faus535.englishtrainer.learningpath.domain;

import java.util.List;
import java.util.Optional;

public interface LearningUnitRepository {

    Optional<LearningUnit> findById(LearningUnitId id);

    List<LearningUnit> findByLearningPathId(LearningPathId learningPathId);

    LearningUnit save(LearningUnit learningUnit);

    void saveAll(List<LearningUnit> learningUnits);
}
