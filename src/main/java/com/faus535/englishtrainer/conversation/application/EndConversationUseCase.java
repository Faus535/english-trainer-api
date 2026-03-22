package com.faus535.englishtrainer.conversation.application;

import com.faus535.englishtrainer.conversation.domain.*;
import com.faus535.englishtrainer.conversation.domain.error.AiTutorException;
import com.faus535.englishtrainer.conversation.domain.error.ConversationAlreadyEndedException;
import com.faus535.englishtrainer.conversation.domain.error.ConversationNotFoundException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@UseCase
public class EndConversationUseCase {

    private final ConversationRepository repository;
    private final AiTutorPort aiTutorPort;
    private final ApplicationEventPublisher eventPublisher;

    public EndConversationUseCase(ConversationRepository repository, AiTutorPort aiTutorPort,
                                   ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.aiTutorPort = aiTutorPort;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Conversation execute(UUID conversationIdValue)
            throws ConversationNotFoundException, ConversationAlreadyEndedException, AiTutorException {

        ConversationId conversationId = new ConversationId(conversationIdValue);
        Conversation conversation = repository.findById(conversationId)
                .orElseThrow(() -> new ConversationNotFoundException(conversationId));

        String summary = aiTutorPort.summarize(conversation.level(), conversation.turns());
        conversation = conversation.end(summary);

        Conversation saved = repository.save(conversation);
        conversation.pullDomainEvents().forEach(eventPublisher::publishEvent);
        return saved;
    }
}
