package com.faus535.englishtrainer.phonetics.application;

import com.faus535.englishtrainer.phonetics.domain.Phoneme;
import com.faus535.englishtrainer.phonetics.domain.PhonemeId;
import com.faus535.englishtrainer.phonetics.domain.PhonemeRepository;
import com.faus535.englishtrainer.phonetics.domain.error.PhonemeNotFoundException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

@UseCase
public class GetPhonemeByIdUseCase {

    private final PhonemeRepository phonemeRepository;

    public GetPhonemeByIdUseCase(PhonemeRepository phonemeRepository) {
        this.phonemeRepository = phonemeRepository;
    }

    @Transactional(readOnly = true)
    public Phoneme execute(PhonemeId id) throws PhonemeNotFoundException {
        return phonemeRepository.findById(id)
                .orElseThrow(() -> new PhonemeNotFoundException(id));
    }
}
