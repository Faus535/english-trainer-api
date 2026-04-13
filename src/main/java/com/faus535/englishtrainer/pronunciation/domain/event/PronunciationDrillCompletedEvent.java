package com.faus535.englishtrainer.pronunciation.domain.event;

import com.faus535.englishtrainer.pronunciation.domain.PronunciationDrillId;
import com.faus535.englishtrainer.shared.domain.event.DomainEvent;

import java.util.UUID;

@DomainEvent
public record PronunciationDrillCompletedEvent(
        PronunciationDrillId drillId,
        UUID userId,
        int score,
        int perfectStreak) {}
