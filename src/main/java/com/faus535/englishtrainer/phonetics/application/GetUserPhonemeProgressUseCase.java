package com.faus535.englishtrainer.phonetics.application;

import com.faus535.englishtrainer.phonetics.domain.*;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@UseCase
public class GetUserPhonemeProgressUseCase {

    private final PhonemeRepository phonemeRepository;
    private final PhonemeDailyAssignmentRepository assignmentRepository;

    public GetUserPhonemeProgressUseCase(PhonemeRepository phonemeRepository,
                                          PhonemeDailyAssignmentRepository assignmentRepository) {
        this.phonemeRepository = phonemeRepository;
        this.assignmentRepository = assignmentRepository;
    }

    public record PhonemeProgressItem(Phoneme phoneme, boolean completed, Instant completedAt) {}

    @Transactional(readOnly = true)
    public List<PhonemeProgressItem> execute(UserProfileId userId) {
        List<Phoneme> allPhonemes = phonemeRepository.findAll();
        List<PhonemeDailyAssignment> completedAssignments = assignmentRepository.findCompletedByUser(userId);

        Map<UUID, PhonemeDailyAssignment> completedMap = new HashMap<>();
        for (PhonemeDailyAssignment a : completedAssignments) {
            completedMap.put(a.phonemeId().value(), a);
        }

        return allPhonemes.stream()
                .sorted(Comparator.comparingInt(Phoneme::difficultyOrder))
                .map(p -> {
                    PhonemeDailyAssignment a = completedMap.get(p.id().value());
                    boolean isCompleted = a != null;
                    Instant completedAtValue = isCompleted ? a.completedAt() : null;
                    return new PhonemeProgressItem(p, isCompleted, completedAtValue);
                })
                .toList();
    }
}
