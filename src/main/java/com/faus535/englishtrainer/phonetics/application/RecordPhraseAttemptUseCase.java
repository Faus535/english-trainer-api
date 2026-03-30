package com.faus535.englishtrainer.phonetics.application;

import com.faus535.englishtrainer.phonetics.domain.*;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.transaction.annotation.Transactional;

@UseCase
public class RecordPhraseAttemptUseCase {

    private final UserPhonemeProgressRepository progressRepository;

    public RecordPhraseAttemptUseCase(UserPhonemeProgressRepository progressRepository) {
        this.progressRepository = progressRepository;
    }

    public record AttemptResult(boolean correct, boolean alreadyRecorded) {}

    @Transactional
    public AttemptResult execute(UserProfileId userId, PhonemeId phonemeId,
                                  PhonemePracticePhraseId phraseId, boolean correct) {
        var existing = progressRepository.findByUserAndPhonemeAndPhrase(userId, phonemeId, phraseId);
        if (existing.isPresent()) {
            return new AttemptResult(existing.get().correct(), true);
        }

        UserPhonemeProgress progress = UserPhonemeProgress.create(userId, phonemeId, phraseId, correct);
        progressRepository.save(progress);
        return new AttemptResult(correct, false);
    }
}
