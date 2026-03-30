package com.faus535.englishtrainer.phonetics.domain.error;

import com.faus535.englishtrainer.phonetics.domain.PhonemePracticePhraseId;

public final class PhraseNotFoundException extends PhoneticsException {
    public PhraseNotFoundException(PhonemePracticePhraseId id) {
        super("Phrase not found: " + id.value());
    }
}
