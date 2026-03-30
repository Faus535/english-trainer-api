package com.faus535.englishtrainer.phonetics.application;

import com.faus535.englishtrainer.phonetics.domain.*;
import com.faus535.englishtrainer.phonetics.domain.error.NoPhonemesAvailableException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@UseCase
public class GetTodayPhonemeUseCase {
    private final PhonemeDailyAssignmentRepository assignmentRepository;
    private final PhonemeRepository phonemeRepository;
    private final UserPhonemeProgressRepository progressRepository;
    private final PhonemePracticePhraseRepository phraseRepository;

    public GetTodayPhonemeUseCase(PhonemeDailyAssignmentRepository assignmentRepository,
                                   PhonemeRepository phonemeRepository,
                                   UserPhonemeProgressRepository progressRepository,
                                   PhonemePracticePhraseRepository phraseRepository) {
        this.assignmentRepository = assignmentRepository;
        this.phonemeRepository = phonemeRepository;
        this.progressRepository = progressRepository;
        this.phraseRepository = phraseRepository;
    }

    public record TodayPhonemeResult(Phoneme phoneme, LocalDate assignedDate,
                                      ProgressSummary progress) {}

    public record ProgressSummary(int attemptsCount, int correctAttemptsCount,
                                   boolean completed, int phrasesCompleted, int phrasesTotal) {}

    @Transactional
    public TodayPhonemeResult execute(UserProfileId userId) throws NoPhonemesAvailableException {
        LocalDate today = LocalDate.now();

        PhonemeDailyAssignment assignment = assignmentRepository.findByUserAndDate(userId, today)
                .orElseGet(() -> assignToday(userId, today));

        Phoneme phoneme = phonemeRepository.findById(assignment.phonemeId())
                .orElseThrow(NoPhonemesAvailableException::new);

        ProgressSummary progress = calculateProgress(userId, assignment.phonemeId(), assignment.completed());

        return new TodayPhonemeResult(phoneme, assignment.assignedDate(), progress);
    }

    private PhonemeDailyAssignment assignToday(UserProfileId userId, LocalDate today) {
        List<Phoneme> uncompleted = phonemeRepository.findUncompletedByUserOrderedByDifficulty(userId.value());
        if (uncompleted.isEmpty()) {
            List<Phoneme> all = phonemeRepository.findAll();
            if (all.isEmpty()) throw new RuntimeException("No phonemes seeded in database");
            Phoneme first = all.getFirst();
            PhonemeDailyAssignment assignment = PhonemeDailyAssignment.create(userId, first.id(), today);
            return assignmentRepository.save(assignment);
        }
        Phoneme next = uncompleted.getFirst();
        PhonemeDailyAssignment assignment = PhonemeDailyAssignment.create(userId, next.id(), today);
        return assignmentRepository.save(assignment);
    }

    private ProgressSummary calculateProgress(UserProfileId userId, PhonemeId phonemeId, boolean completed) {
        List<UserPhonemeProgress> progressList = progressRepository.findByUserAndPhoneme(userId, phonemeId);
        List<PhonemePracticePhrase> phrases = phraseRepository.findByPhonemeId(phonemeId);
        int totalAttempts = progressList.stream().mapToInt(UserPhonemeProgress::attemptsCount).sum();
        int totalCorrect = progressList.stream().mapToInt(UserPhonemeProgress::correctAttemptsCount).sum();
        int phrasesCompleted = (int) progressList.stream().filter(UserPhonemeProgress::phraseCompleted).count();
        return new ProgressSummary(totalAttempts, totalCorrect, completed, phrasesCompleted, phrases.size());
    }
}
