package com.faus535.englishtrainer.phonetics.domain.event;

import com.faus535.englishtrainer.phonetics.domain.PhonemeDailyAssignmentId;
import com.faus535.englishtrainer.phonetics.domain.PhonemeId;
import com.faus535.englishtrainer.shared.domain.event.DomainEvent;
import com.faus535.englishtrainer.user.domain.UserProfileId;

@DomainEvent
public record PhonemeCompletedEvent(PhonemeDailyAssignmentId assignmentId,
                                     UserProfileId userId,
                                     PhonemeId phonemeId) {}
