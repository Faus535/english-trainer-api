package com.faus535.englishtrainer.phonetics.application;

import com.faus535.englishtrainer.phonetics.domain.*;
import com.faus535.englishtrainer.phonetics.infrastructure.InMemoryPhonemeDailyAssignmentRepository;
import com.faus535.englishtrainer.phonetics.infrastructure.InMemoryPhonemeRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

final class GetUserPhonemeProgressUseCaseTest {

    private InMemoryPhonemeRepository phonemeRepository;
    private InMemoryPhonemeDailyAssignmentRepository assignmentRepository;
    private GetUserPhonemeProgressUseCase useCase;

    @BeforeEach
    void setUp() {
        phonemeRepository = new InMemoryPhonemeRepository();
        assignmentRepository = new InMemoryPhonemeDailyAssignmentRepository();
        useCase = new GetUserPhonemeProgressUseCase(phonemeRepository, assignmentRepository);
    }

    @Test
    void shouldReturnEmptyList_whenNoPhonemesExist() {
        UserProfileId userId = UserProfileId.generate();
        List<GetUserPhonemeProgressUseCase.PhonemeProgressItem> result = useCase.execute(userId);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnAllPhonemesWithCompletedFalse_whenNoAssignmentsCompleted() {
        UserProfileId userId = UserProfileId.generate();
        Phoneme p1 = PhonemeMother.withIdAndOrder(PhonemeId.generate(), 1);
        Phoneme p2 = PhonemeMother.withIdAndOrder(PhonemeId.generate(), 2);
        phonemeRepository.addPhoneme(p1);
        phonemeRepository.addPhoneme(p2);

        List<GetUserPhonemeProgressUseCase.PhonemeProgressItem> result = useCase.execute(userId);

        assertEquals(2, result.size());
        assertFalse(result.get(0).completed());
        assertFalse(result.get(1).completed());
        assertNull(result.get(0).completedAt());
    }

    @Test
    void shouldMarkCompletedPhonemes_whenAssignmentsExist() {
        UserProfileId userId = UserProfileId.generate();
        Phoneme p1 = PhonemeMother.withIdAndOrder(PhonemeId.generate(), 1);
        Phoneme p2 = PhonemeMother.withIdAndOrder(PhonemeId.generate(), 2);
        phonemeRepository.addPhoneme(p1);
        phonemeRepository.addPhoneme(p2);

        PhonemeDailyAssignment completed = PhonemeDailyAssignmentMother.completed(userId, p1.id());
        assignmentRepository.save(completed);

        List<GetUserPhonemeProgressUseCase.PhonemeProgressItem> result = useCase.execute(userId);

        assertEquals(2, result.size());
        // Sorted by difficultyOrder
        assertTrue(result.get(0).completed());
        assertNotNull(result.get(0).completedAt());
        assertFalse(result.get(1).completed());
    }
}
