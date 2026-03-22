package com.faus535.englishtrainer.conversation.domain.event;

import com.faus535.englishtrainer.conversation.domain.ConversationId;
import com.faus535.englishtrainer.shared.domain.event.DomainEvent;

import java.util.UUID;

@DomainEvent
public record ConversationStartedEvent(ConversationId conversationId, UUID userId) {}
