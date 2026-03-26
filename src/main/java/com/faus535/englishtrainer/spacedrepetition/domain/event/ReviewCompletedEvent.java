package com.faus535.englishtrainer.spacedrepetition.domain.event;

import com.faus535.englishtrainer.shared.domain.event.DomainEvent;
import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionItemId;
import com.faus535.englishtrainer.user.domain.UserProfileId;

@DomainEvent
public record ReviewCompletedEvent(
        SpacedRepetitionItemId itemId,
        UserProfileId userId,
        boolean graduated,
        String itemType,
        String unitReference
) {

    public ReviewCompletedEvent(SpacedRepetitionItemId itemId, UserProfileId userId) {
        this(itemId, userId, false, "module-unit", null);
    }
}
