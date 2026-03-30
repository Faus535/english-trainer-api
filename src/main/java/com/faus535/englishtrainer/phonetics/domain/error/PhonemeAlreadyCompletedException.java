package com.faus535.englishtrainer.phonetics.domain.error;

import com.faus535.englishtrainer.phonetics.domain.PhonemeId;

public final class PhonemeAlreadyCompletedException extends PhoneticsException {

    public PhonemeAlreadyCompletedException(PhonemeId id) {
        super("Phoneme already completed: " + id.value());
    }
}
