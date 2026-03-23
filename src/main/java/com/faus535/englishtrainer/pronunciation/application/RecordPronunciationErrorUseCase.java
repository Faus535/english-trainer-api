package com.faus535.englishtrainer.pronunciation.application;

import com.faus535.englishtrainer.pronunciation.domain.PronunciationError;
import com.faus535.englishtrainer.pronunciation.domain.PronunciationErrorRepository;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@UseCase
public class RecordPronunciationErrorUseCase {

    private final PronunciationErrorRepository repository;

    public RecordPronunciationErrorUseCase(PronunciationErrorRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public PronunciationError execute(UserProfileId userId, String word,
                                       String expectedPhoneme, String spokenPhoneme) {
        Optional<PronunciationError> existing = repository.findByUserIdAndWordAndPhoneme(
                userId, word, expectedPhoneme);

        if (existing.isPresent()) {
            PronunciationError updated = existing.get().incrementCount();
            return repository.save(updated);
        }

        PronunciationError newError = PronunciationError.create(userId, word, expectedPhoneme, spokenPhoneme);
        return repository.save(newError);
    }
}
