package com.faus535.englishtrainer.phonetics.application;

import com.faus535.englishtrainer.phonetics.domain.*;
import com.faus535.englishtrainer.phonetics.domain.error.PhonemeNotFoundException;
import com.faus535.englishtrainer.phonetics.infrastructure.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

final class GetPhrasesByPhonemeUseCaseTest {
    private InMemoryPhonemeRepository phonemeRepository;
    private InMemoryPhonemePracticePhraseRepository phraseRepository;
    private GetPhrasesByPhonemeUseCase useCase;

    @BeforeEach
    void setUp() {
        phonemeRepository = new InMemoryPhonemeRepository();
        phraseRepository = new InMemoryPhonemePracticePhraseRepository();
        useCase = new GetPhrasesByPhonemeUseCase(phonemeRepository, phraseRepository);
    }

    @Test
    void shouldReturnPhrasesForPhoneme() throws PhonemeNotFoundException {
        Phoneme phoneme = PhonemeMother.random();
        phonemeRepository.addPhoneme(phoneme);
        List<PhonemePracticePhrase> phrases = PhraseMother.fiveForPhoneme(phoneme.id());
        phraseRepository.addAll(phrases);

        List<PhonemePracticePhrase> result = useCase.execute(phoneme.id());

        assertEquals(5, result.size());
    }

    @Test
    void shouldThrowWhenPhonemeNotFound() {
        PhonemeId nonExistent = new PhonemeId(UUID.randomUUID());
        assertThrows(PhonemeNotFoundException.class, () -> useCase.execute(nonExistent));
    }
}
