package com.faus535.englishtrainer.phonetics.domain.error;

import com.faus535.englishtrainer.phonetics.domain.PhonemeId;

public final class PhonemeNotFoundException extends PhoneticsException {
    public PhonemeNotFoundException(PhonemeId id) {
        super("Phoneme not found: " + id.value());
    }
}
