package com.faus535.englishtrainer.phonetics.domain.error;

public final class InsufficientPhrasesCompletedException extends PhoneticsException {
    public InsufficientPhrasesCompletedException(int completed, int required) {
        super("Insufficient phrases completed: " + completed + "/" + required + " required");
    }
}
