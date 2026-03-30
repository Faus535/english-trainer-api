package com.faus535.englishtrainer.phonetics.application;

import com.faus535.englishtrainer.phonetics.domain.*;
import com.faus535.englishtrainer.phonetics.domain.error.PhonemeNotFoundException;
import com.faus535.englishtrainer.phonetics.domain.error.PhraseNotFoundException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
public class RecordPhraseAttemptUseCase {
    private final UserPhonemeProgressRepository progressRepository;
    private final PhonemeRepository phonemeRepository;
    private final PhonemePracticePhraseRepository phraseRepository;

    public RecordPhraseAttemptUseCase(UserPhonemeProgressRepository progressRepository,
                                       PhonemeRepository phonemeRepository,
                                       PhonemePracticePhraseRepository phraseRepository) {
        this.progressRepository = progressRepository;
        this.phonemeRepository = phonemeRepository;
        this.phraseRepository = phraseRepository;
    }

    public record AttemptResult(int attemptsCount, int correctAttemptsCount,
                                 boolean phraseCompleted, boolean phonemeCompleted,
                                 int phrasesCompleted, int phrasesTotal) {}

    @Transactional
    public AttemptResult execute(UserProfileId userId, PhonemeId phonemeId,
                                  PhonemePracticePhraseId phraseId, int score)
            throws PhonemeNotFoundException, PhraseNotFoundException {
        phonemeRepository.findById(phonemeId)
                .orElseThrow(() -> new PhonemeNotFoundException(phonemeId));

        List<PhonemePracticePhrase> phrases = phraseRepository.findByPhonemeId(phonemeId);
        boolean phraseExists = phrases.stream().anyMatch(p -> p.id().equals(phraseId));
        if (!phraseExists) throw new PhraseNotFoundException(phraseId);

        UserPhonemeProgress progress = progressRepository
                .findByUserAndPhonemeAndPhrase(userId, phonemeId, phraseId)
                .orElse(UserPhonemeProgress.create(userId, phonemeId, phraseId));

        UserPhonemeProgress updated = progress.recordAttempt(score);
        progressRepository.save(updated);

        List<UserPhonemeProgress> allProgress = progressRepository.findByUserAndPhoneme(userId, phonemeId);
        int phrasesCompleted = (int) allProgress.stream()
                .filter(UserPhonemeProgress::phraseCompleted).count();
        boolean phonemeCompleted = phrasesCompleted >= 3;

        return new AttemptResult(
            updated.attemptsCount(), updated.correctAttemptsCount(),
            updated.phraseCompleted(), phonemeCompleted,
            phrasesCompleted, phrases.size()
        );
    }
}
