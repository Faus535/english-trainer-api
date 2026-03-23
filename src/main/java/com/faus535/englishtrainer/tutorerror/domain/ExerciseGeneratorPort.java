package com.faus535.englishtrainer.tutorerror.domain;

public interface ExerciseGeneratorPort {

    String generateExercise(String originalText, String correctedText, String errorType) throws Exception;
}
