package com.faus535.englishtrainer.phonetics.application;

import com.faus535.englishtrainer.phonetics.domain.*;
import com.faus535.englishtrainer.phonetics.domain.error.PhonemeNotFoundException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
public class GetPhrasesByPhonemeUseCase {
    private final PhonemeRepository phonemeRepository;
    private final PhonemePracticePhraseRepository phraseRepository;

    public GetPhrasesByPhonemeUseCase(PhonemeRepository phonemeRepository,
                                      PhonemePracticePhraseRepository phraseRepository) {
        this.phonemeRepository = phonemeRepository;
        this.phraseRepository = phraseRepository;
    }

    @Transactional(readOnly = true)
    public List<PhonemePracticePhrase> execute(PhonemeId phonemeId) throws PhonemeNotFoundException {
        phonemeRepository.findById(phonemeId)
                .orElseThrow(() -> new PhonemeNotFoundException(phonemeId));
        return phraseRepository.findByPhonemeId(phonemeId);
    }
}
