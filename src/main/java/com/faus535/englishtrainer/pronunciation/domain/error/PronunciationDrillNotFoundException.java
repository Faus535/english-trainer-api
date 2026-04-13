package com.faus535.englishtrainer.pronunciation.domain.error;

import java.util.UUID;

public final class PronunciationDrillNotFoundException extends PronunciationException {

    public PronunciationDrillNotFoundException(UUID drillId) {
        super("Pronunciation drill not found: " + drillId);
    }
}
