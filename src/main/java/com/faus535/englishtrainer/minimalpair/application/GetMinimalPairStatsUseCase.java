package com.faus535.englishtrainer.minimalpair.application;

import com.faus535.englishtrainer.minimalpair.domain.MinimalPairResultRepository;
import com.faus535.englishtrainer.minimalpair.domain.MinimalPairResultRepository.CategoryAccuracy;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
public class GetMinimalPairStatsUseCase {

    private final MinimalPairResultRepository repository;

    public GetMinimalPairStatsUseCase(MinimalPairResultRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<CategoryAccuracy> execute(UserProfileId userId) {
        return repository.countByUserIdAndCorrectGroupedByCategory(userId);
    }
}
