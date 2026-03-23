package com.faus535.englishtrainer.minimalpair.application;

import com.faus535.englishtrainer.minimalpair.domain.MinimalPairId;
import com.faus535.englishtrainer.minimalpair.domain.MinimalPairResult;
import com.faus535.englishtrainer.minimalpair.domain.MinimalPairResultRepository;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.transaction.annotation.Transactional;

@UseCase
public class RecordMinimalPairResultUseCase {

    private final MinimalPairResultRepository repository;

    public RecordMinimalPairResultUseCase(MinimalPairResultRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public MinimalPairResult execute(UserProfileId userId, MinimalPairId pairId, boolean correct) {
        MinimalPairResult result = MinimalPairResult.create(userId, pairId, correct);
        return repository.save(result);
    }
}
