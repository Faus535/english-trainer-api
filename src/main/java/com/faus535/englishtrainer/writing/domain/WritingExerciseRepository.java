package com.faus535.englishtrainer.writing.domain;

import java.util.List;
import java.util.Optional;

public interface WritingExerciseRepository {

    List<WritingExercise> findByLevel(String level);

    Optional<WritingExercise> findById(WritingExerciseId id);

    WritingExercise save(WritingExercise exercise);
}
