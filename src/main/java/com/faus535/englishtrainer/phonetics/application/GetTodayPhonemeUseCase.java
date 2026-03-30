package com.faus535.englishtrainer.phonetics.application;

import com.faus535.englishtrainer.phonetics.domain.*;
import com.faus535.englishtrainer.phonetics.domain.error.NoPhonemesAvailableException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@UseCase
public class GetTodayPhonemeUseCase {

    private final PhonemeRepository phonemeRepository;
    private final PhonemeDailyAssignmentRepository assignmentRepository;
    private final PhonemePracticePhraseRepository phraseRepository;
    private final UserPhonemeProgressRepository progressRepository;

    public GetTodayPhonemeUseCase(PhonemeRepository phonemeRepository,
                                   PhonemeDailyAssignmentRepository assignmentRepository,
                                   PhonemePracticePhraseRepository phraseRepository,
                                   UserPhonemeProgressRepository progressRepository) {
        this.phonemeRepository = phonemeRepository;
        this.assignmentRepository = assignmentRepository;
        this.phraseRepository = phraseRepository;
        this.progressRepository = progressRepository;
    }

    public record ProgressSummary(int attemptsCount, int correctAttemptsCount,
                                   boolean completed, int phrasesCompleted, int phrasesTotal) {}

    public record TodayPhonemeResult(Phoneme phoneme, LocalDate assignedDate,
                                      ProgressSummary progress,
                                      int completedCount, int totalCount) {}

    @Transactional
    public TodayPhonemeResult execute(UserProfileId userId) throws NoPhonemesAvailableException {
        LocalDate today = LocalDate.now();

        PhonemeDailyAssignment assignment = assignmentRepository.findByUserAndDate(userId, today)
                .orElseGet(() -> createAssignment(userId, today));

        Phoneme phoneme = phonemeRepository.findById(assignment.phonemeId())
                .orElseThrow(NoPhonemesAvailableException::new);

        ProgressSummary progress = buildProgress(userId, phoneme, assignment);

        int completedCount = assignmentRepository.findCompletedByUser(userId).size();
        int totalCount = phonemeRepository.findAll().size();

        return new TodayPhonemeResult(phoneme, assignment.assignedDate(), progress, completedCount, totalCount);
    }

    private PhonemeDailyAssignment createAssignment(UserProfileId userId, LocalDate today) {
        List<Phoneme> uncompleted = phonemeRepository.findUncompletedByUserOrderedByDifficulty(userId);
        if (uncompleted.isEmpty()) {
            List<Phoneme> all = phonemeRepository.findAll();
            if (all.isEmpty()) {
                throw new IllegalStateException("No phonemes available");
            }
            PhonemeDailyAssignment assignment = PhonemeDailyAssignment.create(userId, all.getFirst().id(), today);
            return assignmentRepository.save(assignment);
        }
        PhonemeDailyAssignment assignment = PhonemeDailyAssignment.create(userId, uncompleted.getFirst().id(), today);
        return assignmentRepository.save(assignment);
    }

    private ProgressSummary buildProgress(UserProfileId userId, Phoneme phoneme,
                                           PhonemeDailyAssignment assignment) {
        List<UserPhonemeProgress> attempts = progressRepository.findByUserAndPhoneme(userId, phoneme.id());
        List<PhonemePracticePhrase> phrases = phraseRepository.findByPhonemeId(phoneme.id());

        int attemptsCount = attempts.size();
        int correctAttemptsCount = (int) attempts.stream().filter(UserPhonemeProgress::correct).count();

        Set<UUID> completedPhraseIds = attempts.stream()
                .filter(UserPhonemeProgress::correct)
                .map(a -> a.phraseId().value())
                .collect(Collectors.toSet());

        int phrasesCompleted = completedPhraseIds.size();
        int phrasesTotal = phrases.size();

        return new ProgressSummary(attemptsCount, correctAttemptsCount,
                assignment.completed(), phrasesCompleted, phrasesTotal);
    }
}
