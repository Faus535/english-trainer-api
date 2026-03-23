package com.faus535.englishtrainer.tutorerror.application;

import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.tutorerror.domain.ExerciseGeneratorPort;
import com.faus535.englishtrainer.tutorerror.domain.TutorError;
import com.faus535.englishtrainer.tutorerror.domain.TutorErrorId;
import com.faus535.englishtrainer.tutorerror.domain.TutorErrorRepository;
import com.faus535.englishtrainer.tutorerror.domain.error.TutorErrorException;
import org.springframework.transaction.annotation.Transactional;

@UseCase
public class GenerateErrorExerciseUseCase {

    private final TutorErrorRepository repository;
    private final ExerciseGeneratorPort exerciseGenerator;

    public GenerateErrorExerciseUseCase(TutorErrorRepository repository,
                                         ExerciseGeneratorPort exerciseGenerator) {
        this.repository = repository;
        this.exerciseGenerator = exerciseGenerator;
    }

    @Transactional(readOnly = true)
    public String execute(TutorErrorId errorId) throws Exception {
        TutorError error = repository.findById(errorId)
                .orElseThrow(() -> new TutorErrorException("TutorError not found: " + errorId.value()));

        return exerciseGenerator.generateExercise(
                error.originalText(),
                error.correctedText(),
                error.errorType()
        );
    }
}
