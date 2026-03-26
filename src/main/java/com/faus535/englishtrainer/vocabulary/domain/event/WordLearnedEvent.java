package com.faus535.englishtrainer.vocabulary.domain.event;

import com.faus535.englishtrainer.shared.domain.event.DomainEvent;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.vocabulary.domain.VocabEntryId;
import com.faus535.englishtrainer.vocabulary.domain.VocabMasteryId;

@DomainEvent
public record WordLearnedEvent(VocabMasteryId masteryId, UserProfileId userId, VocabEntryId vocabEntryId, String word) {}
