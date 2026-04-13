package com.faus535.englishtrainer.pronunciation.domain.event;

import com.faus535.englishtrainer.pronunciation.domain.PronunciationMiniConversationId;
import com.faus535.englishtrainer.shared.domain.event.DomainEvent;

import java.util.UUID;

@DomainEvent
public record PronunciationMiniConversationCompletedEvent(
        PronunciationMiniConversationId conversationId,
        UUID userId,
        int finalScore) {}
