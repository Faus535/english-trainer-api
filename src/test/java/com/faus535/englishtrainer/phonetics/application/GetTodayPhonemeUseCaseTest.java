package com.faus535.englishtrainer.phonetics.application;

import com.faus535.englishtrainer.phonetics.domain.*;
import com.faus535.englishtrainer.phonetics.domain.error.NoPhonemesAvailableException;
import com.faus535.englishtrainer.phonetics.infrastructure.*;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

final class GetTodayPhonemeUseCaseTest {
    private InMemoryPhonemeDailyAssignmentRepository assignmentRepository;
    private InMemoryPhonemeRepository phonemeRepository;
    private InMemoryUserPhonemeProgressRepository progressRepository;
    private InMemoryPhonemePracticePhraseRepository phraseRepository;
    private GetTodayPhonemeUseCase useCase;

    @BeforeEach
    void setUp() {
        assignmentRepository = new InMemoryPhonemeDailyAssignmentRepository();
        phonemeRepository = new InMemoryPhonemeRepository();
        progressRepository = new InMemoryUserPhonemeProgressRepository();
        phraseRepository = new InMemoryPhonemePracticePhraseRepository();
        useCase = new GetTodayPhonemeUseCase(assignmentRepository, phonemeRepository,
                                              progressRepository, phraseRepository);
    }

    @Test
    void shouldAssignFirstUncompletedPhoneme_whenNoAssignmentExists()
            throws NoPhonemesAvailableException {
        UserProfileId userId = UserProfileId.generate();
        Phoneme phoneme = PhonemeMother.random();
        phonemeRepository.addPhoneme(phoneme);
        phraseRepository.addAll(PhraseMother.fiveForPhoneme(phoneme.id()));

        GetTodayPhonemeUseCase.TodayPhonemeResult result = useCase.execute(userId);

        assertEquals(phoneme.id(), result.phoneme().id());
        assertEquals(LocalDate.now(), result.assignedDate());
        assertFalse(result.progress().completed());
    }

    @Test
    void shouldReturnExistingAssignment_whenAlreadyAssignedToday()
            throws NoPhonemesAvailableException {
        UserProfileId userId = UserProfileId.generate();
        Phoneme phoneme = PhonemeMother.random();
        phonemeRepository.addPhoneme(phoneme);
        phraseRepository.addAll(PhraseMother.fiveForPhoneme(phoneme.id()));

        PhonemeDailyAssignment existing = PhonemeDailyAssignment.create(userId, phoneme.id(), LocalDate.now());
        assignmentRepository.save(existing);

        GetTodayPhonemeUseCase.TodayPhonemeResult result = useCase.execute(userId);

        assertEquals(phoneme.id(), result.phoneme().id());
    }
}
