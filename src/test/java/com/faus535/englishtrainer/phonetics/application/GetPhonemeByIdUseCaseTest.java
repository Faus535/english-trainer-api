package com.faus535.englishtrainer.phonetics.application;

import com.faus535.englishtrainer.phonetics.domain.*;
import com.faus535.englishtrainer.phonetics.domain.error.PhonemeNotFoundException;
import com.faus535.englishtrainer.phonetics.infrastructure.InMemoryPhonemeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

final class GetPhonemeByIdUseCaseTest {
    private InMemoryPhonemeRepository repository;
    private GetPhonemeByIdUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryPhonemeRepository();
        useCase = new GetPhonemeByIdUseCase(repository);
    }

    @Test
    void shouldReturnPhonemeWhenFound() throws PhonemeNotFoundException {
        Phoneme phoneme = PhonemeMother.random();
        repository.addPhoneme(phoneme);

        Phoneme result = useCase.execute(phoneme.id());

        assertEquals(phoneme.id(), result.id());
        assertEquals(phoneme.symbol(), result.symbol());
    }

    @Test
    void shouldThrowWhenPhonemeNotFound() {
        PhonemeId nonExistent = new PhonemeId(UUID.randomUUID());
        assertThrows(PhonemeNotFoundException.class, () -> useCase.execute(nonExistent));
    }
}
