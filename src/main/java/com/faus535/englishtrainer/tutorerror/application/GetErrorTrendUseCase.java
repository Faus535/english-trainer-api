package com.faus535.englishtrainer.tutorerror.application;

import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.tutorerror.domain.TutorErrorRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@UseCase
public class GetErrorTrendUseCase {

    private final TutorErrorRepository repository;

    public GetErrorTrendUseCase(TutorErrorRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> execute(UserProfileId userId, int weeks) {
        return repository.countByUserIdGroupedByWeek(userId, weeks);
    }
}
