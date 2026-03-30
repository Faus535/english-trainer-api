package com.faus535.englishtrainer.phonetics.application;

import com.faus535.englishtrainer.phonetics.domain.Phoneme;
import com.faus535.englishtrainer.phonetics.domain.PhonemeRepository;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
public class GetAllPhonemesUseCase {

    private final PhonemeRepository phonemeRepository;

    public GetAllPhonemesUseCase(PhonemeRepository phonemeRepository) {
        this.phonemeRepository = phonemeRepository;
    }

    @Transactional(readOnly = true)
    public List<Phoneme> execute() {
        return phonemeRepository.findAll();
    }
}
