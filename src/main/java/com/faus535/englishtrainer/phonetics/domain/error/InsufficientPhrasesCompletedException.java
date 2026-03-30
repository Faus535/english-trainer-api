package com.faus535.englishtrainer.phonetics.domain.error;

import com.faus535.englishtrainer.phonetics.domain.PhonemeId;

public final class InsufficientPhrasesCompletedException extends PhoneticsException {

    public InsufficientPhrasesCompletedException(PhonemeId phonemeId, int completed, int required) {
        super("Phoneme " + phonemeId.value() + " requires " + required +
              " completed phrases but only has " + completed);
    }
}
