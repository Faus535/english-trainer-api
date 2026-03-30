package com.faus535.englishtrainer.phonetics.domain;

import java.util.List;

public interface PhonemePracticePhraseRepository {

    List<PhonemePracticePhrase> findByPhonemeId(PhonemeId phonemeId);
}
