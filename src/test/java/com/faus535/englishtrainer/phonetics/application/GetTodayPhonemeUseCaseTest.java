package com.faus535.englishtrainer.phonetics.application;

import com.faus535.englishtrainer.phonetics.domain.*;
import com.faus535.englishtrainer.phonetics.domain.error.NoPhonemesAvailableException;
import com.faus535.englishtrainer.phonetics.infrastructure.*;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class GetTodayPhonemeUseCaseTest {

    private InMemoryPhonemeRepository phonemeRepository;
    private InMemoryPhonemeDailyAssignmentRepository assignmentRepository;
    private InMemoryPhonemePracticePhraseRepository phraseRepository;
    private InMemoryUserPhonemeProgressRepository progressRepository;
    private GetTodayPhonemeUseCase useCase;

    @BeforeEach
    void setUp() {
        phonemeRepository = new InMemoryPhonemeRepository();
        assignmentRepository = new InMemoryPhonemeDailyAssignmentRepository();
        phraseRepository = new InMemoryPhonemePracticePhraseRepository();
        progressRepository = new InMemoryUserPhonemeProgressRepository();
        useCase = new GetTodayPhonemeUseCase(phonemeRepository, assignmentRepository,
                phraseRepository, progressRepository);
    }

    @Test
    void shouldReturnCompletedAndTotalCounts_whenAssignmentsExist()
            throws NoPhonemesAvailableException {
        UserProfileId userId = UserProfileId.generate();
        Phoneme phoneme1 = PhonemeMother.withIdAndOrder(PhonemeId.generate(), 1);
        Phoneme phoneme2 = PhonemeMother.withIdAndOrder(PhonemeId.generate(), 2);
        phonemeRepository.addPhoneme(phoneme1);
        phonemeRepository.addPhoneme(phoneme2);
        phraseRepository.addAll(PhraseMother.fiveForPhoneme(phoneme1.id()));
        phraseRepository.addAll(PhraseMother.fiveForPhoneme(phoneme2.id()));

        // Complete phoneme1
        PhonemeDailyAssignment completed = PhonemeDailyAssignmentMother.completed(userId, phoneme1.id());
        assignmentRepository.save(completed);

        GetTodayPhonemeUseCase.TodayPhonemeResult result = useCase.execute(userId);

        assertEquals(1, result.completedCount());
        assertEquals(2, result.totalCount());
    }

    @Test
    void shouldReturnZeroCompletedCount_whenNoCompletedAssignments()
            throws NoPhonemesAvailableException {
        UserProfileId userId = UserProfileId.generate();
        Phoneme phoneme = PhonemeMother.random();
        phonemeRepository.addPhoneme(phoneme);
        phraseRepository.addAll(PhraseMother.fiveForPhoneme(phoneme.id()));

        GetTodayPhonemeUseCase.TodayPhonemeResult result = useCase.execute(userId);

        assertEquals(0, result.completedCount());
        assertEquals(1, result.totalCount());
    }
}
