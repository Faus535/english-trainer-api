package com.faus535.englishtrainer.writing.application;

import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.writing.domain.WritingExercise;
import com.faus535.englishtrainer.writing.domain.WritingExerciseRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
public class GetWritingExercisesUseCase {

    private final WritingExerciseRepository repository;

    public GetWritingExercisesUseCase(WritingExerciseRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<WritingExercise> execute(String level) {
        return repository.findByLevel(level.toLowerCase());
    }
}
