package com.faus535.englishtrainer.tutorerror.application;

import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.tutorerror.domain.TutorError;
import com.faus535.englishtrainer.tutorerror.domain.TutorErrorRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@UseCase
public class RecordTutorErrorUseCase {

    private final TutorErrorRepository repository;

    public RecordTutorErrorUseCase(TutorErrorRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public TutorError execute(UserProfileId userId, String errorType, String originalText,
                               String correctedText, String rule) {
        Optional<TutorError> existing = repository.findByUserIdAndOriginalAndCorrected(
                userId, originalText, correctedText);

        if (existing.isPresent()) {
            TutorError updated = existing.get().incrementCount();
            return repository.save(updated);
        }

        TutorError newError = TutorError.create(userId, errorType.toUpperCase(),
                originalText, correctedText, rule);
        return repository.save(newError);
    }
}
