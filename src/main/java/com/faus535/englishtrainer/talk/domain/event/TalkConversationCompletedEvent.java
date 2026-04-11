package com.faus535.englishtrainer.talk.domain.event;

import com.faus535.englishtrainer.shared.domain.event.DomainEvent;
import com.faus535.englishtrainer.talk.domain.TalkConversationId;
import com.faus535.englishtrainer.talk.domain.TalkCorrection;

import java.util.List;
import java.util.UUID;

@DomainEvent
public record TalkConversationCompletedEvent(
        TalkConversationId conversationId,
        UUID userId,
        List<TalkCorrection> corrections,
        int turnCount
) {}
