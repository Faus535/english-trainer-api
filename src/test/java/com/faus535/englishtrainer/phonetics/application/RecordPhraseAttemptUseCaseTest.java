package com.faus535.englishtrainer.phonetics.application;

import com.faus535.englishtrainer.phonetics.domain.*;
import com.faus535.englishtrainer.phonetics.domain.error.PhonemeNotFoundException;
import com.faus535.englishtrainer.phonetics.domain.error.PhraseNotFoundException;
import com.faus535.englishtrainer.phonetics.infrastructure.*;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

final class RecordPhraseAttemptUseCaseTest {
    private InMemoryUserPhonemeProgressRepository progressRepository;
    private InMemoryPhonemeRepository phonemeRepository;
    private InMemoryPhonemePracticePhraseRepository phraseRepository;
    private RecordPhraseAttemptUseCase useCase;

    @BeforeEach
    void setUp() {
        progressRepository = new InMemoryUserPhonemeProgressRepository();
        phonemeRepository = new InMemoryPhonemeRepository();
        phraseRepository = new InMemoryPhonemePracticePhraseRepository();
        useCase = new RecordPhraseAttemptUseCase(progressRepository, phonemeRepository, phraseRepository);
    }

    @Test
    void shouldRecordAttemptAndCreateProgress()
            throws PhonemeNotFoundException, PhraseNotFoundException {
        UserProfileId userId = UserProfileId.generate();
        Phoneme phoneme = PhonemeMother.random();
        phonemeRepository.addPhoneme(phoneme);
        PhonemePracticePhrase phrase = PhraseMother.random(phoneme.id());
        phraseRepository.addPhrase(phrase);

        RecordPhraseAttemptUseCase.AttemptResult result =
                useCase.execute(userId, phoneme.id(), phrase.id(), 75);

        assertEquals(1, result.attemptsCount());
        assertEquals(1, result.correctAttemptsCount());
        assertTrue(result.phraseCompleted());
    }

    @Test
    void shouldNotCompletePhraseOnLowScore()
            throws PhonemeNotFoundException, PhraseNotFoundException {
        UserProfileId userId = UserProfileId.generate();
        Phoneme phoneme = PhonemeMother.random();
        phonemeRepository.addPhoneme(phoneme);
        PhonemePracticePhrase phrase = PhraseMother.random(phoneme.id());
        phraseRepository.addPhrase(phrase);

        RecordPhraseAttemptUseCase.AttemptResult result =
                useCase.execute(userId, phoneme.id(), phrase.id(), 40);

        assertEquals(1, result.attemptsCount());
        assertEquals(0, result.correctAttemptsCount());
        assertFalse(result.phraseCompleted());
    }

    @Test
    void shouldDetectPhonemeCompletionWhenThreePhrasesCompleted()
            throws PhonemeNotFoundException, PhraseNotFoundException {
        UserProfileId userId = UserProfileId.generate();
        Phoneme phoneme = PhonemeMother.random();
        phonemeRepository.addPhoneme(phoneme);
        List<PhonemePracticePhrase> phrases = PhraseMother.fiveForPhoneme(phoneme.id());
        phraseRepository.addAll(phrases);

        for (int i = 0; i < 2; i++) {
            UserPhonemeProgress completed = UserPhonemeProgressMother.completed(
                    userId, phoneme.id(), phrases.get(i).id());
            progressRepository.save(completed);
        }

        RecordPhraseAttemptUseCase.AttemptResult result =
                useCase.execute(userId, phoneme.id(), phrases.get(2).id(), 80);

        assertTrue(result.phraseCompleted());
        assertTrue(result.phonemeCompleted());
        assertEquals(3, result.phrasesCompleted());
    }

    @Test
    void shouldThrowWhenPhonemeNotFound() {
        UserProfileId userId = UserProfileId.generate();
        PhonemeId badId = new PhonemeId(UUID.randomUUID());
        PhonemePracticePhraseId phraseId = new PhonemePracticePhraseId(UUID.randomUUID());
        assertThrows(PhonemeNotFoundException.class,
                () -> useCase.execute(userId, badId, phraseId, 80));
    }

    @Test
    void shouldThrowWhenPhraseNotFound() {
        UserProfileId userId = UserProfileId.generate();
        Phoneme phoneme = PhonemeMother.random();
        phonemeRepository.addPhoneme(phoneme);
        PhonemePracticePhraseId badPhraseId = new PhonemePracticePhraseId(UUID.randomUUID());
        assertThrows(PhraseNotFoundException.class,
                () -> useCase.execute(userId, phoneme.id(), badPhraseId, 80));
    }
}
