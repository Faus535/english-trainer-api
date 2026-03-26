package com.faus535.englishtrainer.vocabulary.domain.event;

import com.faus535.englishtrainer.shared.domain.event.DomainEvent;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.vocabulary.domain.VocabMasteryId;

@DomainEvent
public record WordMasteredEvent(VocabMasteryId masteryId, UserProfileId userId, String word) {}
