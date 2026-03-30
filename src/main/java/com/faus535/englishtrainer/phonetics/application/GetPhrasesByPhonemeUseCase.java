package com.faus535.englishtrainer.phonetics.application;

import com.faus535.englishtrainer.phonetics.domain.PhonemeId;
import com.faus535.englishtrainer.phonetics.domain.PhonemePracticePhrase;
import com.faus535.englishtrainer.phonetics.domain.PhonemePracticePhraseRepository;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
public class GetPhrasesByPhonemeUseCase {

    private final PhonemePracticePhraseRepository phraseRepository;

    public GetPhrasesByPhonemeUseCase(PhonemePracticePhraseRepository phraseRepository) {
        this.phraseRepository = phraseRepository;
    }

    @Transactional(readOnly = true)
    public List<PhonemePracticePhrase> execute(PhonemeId phonemeId) {
        return phraseRepository.findByPhonemeId(phonemeId);
    }
}
