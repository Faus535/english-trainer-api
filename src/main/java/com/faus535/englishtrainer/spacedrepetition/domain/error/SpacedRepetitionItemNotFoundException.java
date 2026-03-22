package com.faus535.englishtrainer.spacedrepetition.domain.error;

import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionItemId;

public final class SpacedRepetitionItemNotFoundException extends SpacedRepetitionException {

    public SpacedRepetitionItemNotFoundException(SpacedRepetitionItemId id) {
        super("Spaced repetition item not found: " + id.value());
    }
}
