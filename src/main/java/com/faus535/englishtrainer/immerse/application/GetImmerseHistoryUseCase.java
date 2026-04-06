package com.faus535.englishtrainer.immerse.application;

import com.faus535.englishtrainer.immerse.domain.ImmerseContent;
import com.faus535.englishtrainer.immerse.domain.ImmerseContentRepository;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@UseCase
public class GetImmerseHistoryUseCase {

    private final ImmerseContentRepository repository;

    GetImmerseHistoryUseCase(ImmerseContentRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<ImmerseContent> execute(UUID userId, int page, int size) {
        return repository.findByUserId(userId, page, size);
    }
}
