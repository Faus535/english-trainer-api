package com.faus535.englishtrainer.phonetics.application;

import com.faus535.englishtrainer.phonetics.domain.*;
import com.faus535.englishtrainer.phonetics.domain.error.InsufficientPhrasesCompletedException;
import com.faus535.englishtrainer.phonetics.domain.error.PhonemeAlreadyCompletedException;
import com.faus535.englishtrainer.phonetics.domain.error.PhonemeNotFoundException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@UseCase
public class CompletePhonemeUseCase {

    private static final int REQUIRED_PHRASES = 3;

    private final PhonemeDailyAssignmentRepository assignmentRepository;
    private final UserPhonemeProgressRepository progressRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CompletePhonemeUseCase(PhonemeDailyAssignmentRepository assignmentRepository,
                                   UserPhonemeProgressRepository progressRepository,
                                   ApplicationEventPublisher eventPublisher) {
        this.assignmentRepository = assignmentRepository;
        this.progressRepository = progressRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void execute(UserProfileId userId, PhonemeId phonemeId)
            throws PhonemeNotFoundException, PhonemeAlreadyCompletedException,
                   InsufficientPhrasesCompletedException {
        PhonemeDailyAssignment assignment = assignmentRepository.findByUserAndPhoneme(userId, phonemeId)
                .orElseThrow(() -> new PhonemeNotFoundException(phonemeId));

        if (assignment.completed()) {
            throw new PhonemeAlreadyCompletedException(phonemeId);
        }

        List<UserPhonemeProgress> attempts = progressRepository.findByUserAndPhoneme(userId, phonemeId);
        Set<UUID> completedPhraseIds = attempts.stream()
                .filter(UserPhonemeProgress::correct)
                .map(a -> a.phraseId().value())
                .collect(Collectors.toSet());

        if (completedPhraseIds.size() < REQUIRED_PHRASES) {
            throw new InsufficientPhrasesCompletedException(phonemeId,
                    completedPhraseIds.size(), REQUIRED_PHRASES);
        }

        PhonemeDailyAssignment completed = assignment.complete();
        assignmentRepository.save(completed);
        completed.pullDomainEvents().forEach(eventPublisher::publishEvent);
    }
}
