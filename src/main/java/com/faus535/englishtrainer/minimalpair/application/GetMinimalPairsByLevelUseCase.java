package com.faus535.englishtrainer.minimalpair.application;

import com.faus535.englishtrainer.minimalpair.domain.MinimalPair;
import com.faus535.englishtrainer.minimalpair.domain.MinimalPairRepository;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
public class GetMinimalPairsByLevelUseCase {

    private final MinimalPairRepository repository;

    public GetMinimalPairsByLevelUseCase(MinimalPairRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<MinimalPair> execute(String level, String soundCategory, int limit) {
        if (soundCategory != null && !soundCategory.isBlank()) {
            return repository.findByLevelAndCategory(level, soundCategory, limit);
        }
        return repository.findByLevel(level, limit);
    }
}
