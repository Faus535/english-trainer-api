package com.faus535.englishtrainer.immerse.domain;

import java.util.List;
import java.util.Optional;

public interface ImmerseExerciseRepository {

    void saveAll(List<ImmerseExercise> exercises);

    List<ImmerseExercise> findByContentId(ImmerseContentId contentId);

    List<ImmerseExercise> findByContentIdAndTypeFilter(ImmerseContentId contentId, ExerciseTypeFilter filter);

    Optional<ImmerseExercise> findById(ImmerseExerciseId id);
}
