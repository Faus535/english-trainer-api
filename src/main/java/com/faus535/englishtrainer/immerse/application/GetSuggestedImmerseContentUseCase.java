package com.faus535.englishtrainer.immerse.application;

import com.faus535.englishtrainer.immerse.domain.ImmerseContent;
import com.faus535.englishtrainer.immerse.domain.ImmerseContentRepository;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;

import java.util.Optional;
import java.util.UUID;

@UseCase
public class GetSuggestedImmerseContentUseCase {

    private final ImmerseContentRepository repository;

    public GetSuggestedImmerseContentUseCase(ImmerseContentRepository repository) {
        this.repository = repository;
    }

    public Optional<ImmerseContent> execute(UUID userId) {
        return repository.findLatestByUserId(userId);
    }
}
