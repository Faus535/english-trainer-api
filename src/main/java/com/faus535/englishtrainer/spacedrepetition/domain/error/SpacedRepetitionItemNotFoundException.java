package com.faus535.englishtrainer.spacedrepetition.domain.error;

import com.faus535.englishtrainer.shared.domain.error.NotFoundException;
import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionItemId;

public class SpacedRepetitionItemNotFoundException extends NotFoundException {

    public SpacedRepetitionItemNotFoundException(SpacedRepetitionItemId id) {
        super("Spaced repetition item not found: " + id.value());
    }
}
