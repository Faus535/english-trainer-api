package com.faus535.englishtrainer.exercise.domain;

import java.util.List;

public interface ExerciseGeneratorPort {
    List<Exercise> generate(String level, List<String> errors) throws Exception;
}
