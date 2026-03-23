package com.faus535.englishtrainer.pronunciation.application;

import com.faus535.englishtrainer.pronunciation.domain.PronunciationError;
import com.faus535.englishtrainer.pronunciation.domain.PronunciationErrorRepository;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@UseCase
public class GetFrequentErrorsUseCase {

    private final PronunciationErrorRepository repository;

    public GetFrequentErrorsUseCase(PronunciationErrorRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<PronunciationError> execute(UserProfileId userId, int limit) {
        return repository.findByUserId(userId).stream()
                .sorted(Comparator.comparingInt(PronunciationError::occurrenceCount).reversed())
                .limit(limit)
                .toList();
    }
}
