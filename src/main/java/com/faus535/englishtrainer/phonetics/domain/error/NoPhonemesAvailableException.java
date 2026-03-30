package com.faus535.englishtrainer.phonetics.domain.error;

public final class NoPhonemesAvailableException extends PhoneticsException {
    public NoPhonemesAvailableException() {
        super("All phonemes have been completed. No more phonemes available.");
    }
}
