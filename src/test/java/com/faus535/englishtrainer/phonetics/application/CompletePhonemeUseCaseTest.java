package com.faus535.englishtrainer.phonetics.application;

import com.faus535.englishtrainer.phonetics.domain.*;
import com.faus535.englishtrainer.phonetics.domain.error.*;
import com.faus535.englishtrainer.phonetics.infrastructure.*;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

final class CompletePhonemeUseCaseTest {
    private InMemoryPhonemeDailyAssignmentRepository assignmentRepository;
    private InMemoryUserPhonemeProgressRepository progressRepository;
    private InMemoryPhonemeRepository phonemeRepository;
    private CompletePhonemeUseCase useCase;

    @BeforeEach
    void setUp() {
        assignmentRepository = new InMemoryPhonemeDailyAssignmentRepository();
        progressRepository = new InMemoryUserPhonemeProgressRepository();
        phonemeRepository = new InMemoryPhonemeRepository();
        useCase = new CompletePhonemeUseCase(assignmentRepository, progressRepository, phonemeRepository);
    }

    @Test
    void shouldCompletePhoneme_whenThreePhrasesCompleted()
            throws PhonemeNotFoundException, PhonemeAlreadyCompletedException,
                   InsufficientPhrasesCompletedException {
        UserProfileId userId = UserProfileId.generate();
        Phoneme phoneme = PhonemeMother.random();
        phonemeRepository.addPhoneme(phoneme);

        PhonemeDailyAssignment assignment = PhonemeDailyAssignmentMother.create(userId, phoneme.id());
        assignmentRepository.save(assignment);

        List<PhonemePracticePhrase> phrases = PhraseMother.fiveForPhoneme(phoneme.id());

        for (int i = 0; i < 3; i++) {
            progressRepository.save(
                UserPhonemeProgressMother.completed(userId, phoneme.id(), phrases.get(i).id())
            );
        }

        CompletePhonemeUseCase.CompletionResult result = useCase.execute(userId, phoneme.id());

        assertTrue(result.completed());
        assertNotNull(result.completedAt());
    }

    @Test
    void shouldThrow_whenInsufficientPhrasesCompleted() {
        UserProfileId userId = UserProfileId.generate();
        Phoneme phoneme = PhonemeMother.random();
        phonemeRepository.addPhoneme(phoneme);

        PhonemeDailyAssignment assignment = PhonemeDailyAssignmentMother.create(userId, phoneme.id());
        assignmentRepository.save(assignment);

        assertThrows(InsufficientPhrasesCompletedException.class,
                () -> useCase.execute(userId, phoneme.id()));
    }

    @Test
    void shouldThrow_whenAlreadyCompleted() {
        UserProfileId userId = UserProfileId.generate();
        Phoneme phoneme = PhonemeMother.random();
        phonemeRepository.addPhoneme(phoneme);

        PhonemeDailyAssignment completed = PhonemeDailyAssignmentMother.completed(userId, phoneme.id());
        assignmentRepository.save(completed);

        assertThrows(PhonemeAlreadyCompletedException.class,
                () -> useCase.execute(userId, phoneme.id()));
    }

    @Test
    void shouldThrow_whenPhonemeNotFound() {
        UserProfileId userId = UserProfileId.generate();
        PhonemeId badId = new PhonemeId(UUID.randomUUID());
        assertThrows(PhonemeNotFoundException.class,
                () -> useCase.execute(userId, badId));
    }
}
