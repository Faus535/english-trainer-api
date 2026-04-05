package com.faus535.englishtrainer.immerse.application;

import com.faus535.englishtrainer.immerse.domain.*;
import com.faus535.englishtrainer.immerse.domain.error.ImmerseContentNotFoundException;
import com.faus535.englishtrainer.immerse.domain.error.ImmerseContentNotProcessedException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;

import java.util.List;
import java.util.UUID;

@UseCase
public class GetImmerseExercisesUseCase {

    private final ImmerseContentRepository contentRepository;
    private final ImmerseExerciseRepository exerciseRepository;

    GetImmerseExercisesUseCase(ImmerseContentRepository contentRepository,
                                ImmerseExerciseRepository exerciseRepository) {
        this.contentRepository = contentRepository;
        this.exerciseRepository = exerciseRepository;
    }

    public List<ImmerseExercise> execute(UUID contentId)
            throws ImmerseContentNotFoundException, ImmerseContentNotProcessedException {

        ImmerseContentId id = new ImmerseContentId(contentId);
        ImmerseContent content = contentRepository.findById(id)
                .orElseThrow(() -> new ImmerseContentNotFoundException(id));

        if (content.status() != ImmerseContentStatus.PROCESSED) {
            throw new ImmerseContentNotProcessedException(id);
        }

        return exerciseRepository.findByContentId(id);
    }
}
