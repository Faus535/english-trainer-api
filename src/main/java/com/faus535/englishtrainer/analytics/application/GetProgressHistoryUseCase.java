package com.faus535.englishtrainer.analytics.application;

import com.faus535.englishtrainer.analytics.domain.LevelHistoryEntry;
import com.faus535.englishtrainer.analytics.domain.LevelHistoryRepository;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@UseCase
public class GetProgressHistoryUseCase {

    private final LevelHistoryRepository repository;

    public GetProgressHistoryUseCase(LevelHistoryRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<LevelHistoryEntry> execute(UUID userId) {
        return repository.findByUserId(userId);
    }
}
