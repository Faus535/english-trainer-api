package com.faus535.englishtrainer.tutorerror.application;

import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.tutorerror.domain.TutorError;
import com.faus535.englishtrainer.tutorerror.domain.TutorErrorRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UseCase
public class GetUserErrorsUseCase {

    private final TutorErrorRepository repository;

    public GetUserErrorsUseCase(TutorErrorRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Map<String, List<TutorError>> execute(UserProfileId userId, String type, int limit) {
        List<TutorError> errors;
        if (type != null && !type.isBlank()) {
            errors = repository.findByUserIdAndType(userId, type.toUpperCase());
        } else {
            errors = repository.findByUserId(userId);
        }

        return errors.stream()
                .sorted((a, b) -> Integer.compare(b.occurrenceCount(), a.occurrenceCount()))
                .limit(limit)
                .collect(Collectors.groupingBy(TutorError::errorType));
    }
}
