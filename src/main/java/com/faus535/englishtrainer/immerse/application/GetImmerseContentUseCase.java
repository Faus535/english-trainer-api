package com.faus535.englishtrainer.immerse.application;

import com.faus535.englishtrainer.immerse.domain.*;
import com.faus535.englishtrainer.immerse.domain.error.ImmerseContentAccessDeniedException;
import com.faus535.englishtrainer.immerse.domain.error.ImmerseContentNotFoundException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;

import java.util.UUID;

@UseCase
public class GetImmerseContentUseCase {

    private final ImmerseContentRepository repository;

    GetImmerseContentUseCase(ImmerseContentRepository repository) {
        this.repository = repository;
    }

    public ImmerseContent execute(UUID contentId, UUID userId)
            throws ImmerseContentNotFoundException, ImmerseContentAccessDeniedException {

        ImmerseContentId id = new ImmerseContentId(contentId);
        ImmerseContent content = repository.findById(id)
                .orElseThrow(() -> new ImmerseContentNotFoundException(id));

        if (!content.userId().equals(userId)) {
            throw new ImmerseContentAccessDeniedException(id);
        }

        return content;
    }
}
