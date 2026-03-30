package com.faus535.englishtrainer.phonetics.application;

import com.faus535.englishtrainer.phonetics.domain.*;
import com.faus535.englishtrainer.phonetics.domain.error.*;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@UseCase
public class CompletePhonemeUseCase {
    private static final int REQUIRED_PHRASES = 3;

    private final PhonemeDailyAssignmentRepository assignmentRepository;
    private final UserPhonemeProgressRepository progressRepository;
    private final PhonemeRepository phonemeRepository;

    public CompletePhonemeUseCase(PhonemeDailyAssignmentRepository assignmentRepository,
                                   UserPhonemeProgressRepository progressRepository,
                                   PhonemeRepository phonemeRepository) {
        this.assignmentRepository = assignmentRepository;
        this.progressRepository = progressRepository;
        this.phonemeRepository = phonemeRepository;
    }

    public record CompletionResult(boolean completed, Instant completedAt) {}

    @Transactional
    public CompletionResult execute(UserProfileId userId, PhonemeId phonemeId)
            throws PhonemeNotFoundException, PhonemeAlreadyCompletedException,
                   InsufficientPhrasesCompletedException {
        phonemeRepository.findById(phonemeId)
                .orElseThrow(() -> new PhonemeNotFoundException(phonemeId));

        PhonemeDailyAssignment assignment = assignmentRepository
                .findByUserAndPhoneme(userId, phonemeId)
                .orElseThrow(() -> new PhonemeNotFoundException(phonemeId));

        if (assignment.completed()) {
            throw new PhonemeAlreadyCompletedException(phonemeId);
        }

        List<UserPhonemeProgress> progressList = progressRepository.findByUserAndPhoneme(userId, phonemeId);
        int phrasesCompleted = (int) progressList.stream()
                .filter(UserPhonemeProgress::phraseCompleted).count();

        if (phrasesCompleted < REQUIRED_PHRASES) {
            throw new InsufficientPhrasesCompletedException(phrasesCompleted, REQUIRED_PHRASES);
        }

        PhonemeDailyAssignment completed = assignment.complete();
        assignmentRepository.save(completed);

        return new CompletionResult(true, completed.completedAt());
    }
}
