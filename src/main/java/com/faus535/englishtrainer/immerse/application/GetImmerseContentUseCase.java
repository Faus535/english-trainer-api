package com.faus535.englishtrainer.immerse.application;

import com.faus535.englishtrainer.immerse.domain.*;
import com.faus535.englishtrainer.immerse.domain.error.ImmerseContentNotFoundException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;

import java.util.UUID;

@UseCase
public class GetImmerseContentUseCase {

    private final ImmerseContentRepository repository;

    GetImmerseContentUseCase(ImmerseContentRepository repository) {
        this.repository = repository;
    }

    public ImmerseContent execute(UUID contentId) throws ImmerseContentNotFoundException {
        return repository.findById(new ImmerseContentId(contentId))
                .orElseThrow(() -> new ImmerseContentNotFoundException(new ImmerseContentId(contentId)));
    }
}
