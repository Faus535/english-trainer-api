package com.faus535.englishtrainer.phonetics.domain.error;

public final class NoPhonemesAvailableException extends PhoneticsException {

    public NoPhonemesAvailableException() {
        super("No phonemes available for practice");
    }
}
