package com.faus535.englishtrainer.conversation.application;

import com.faus535.englishtrainer.conversation.domain.*;
import com.faus535.englishtrainer.conversation.domain.error.AiTutorException;
import com.faus535.englishtrainer.conversation.domain.error.MaxConversationsExceededException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@UseCase
public class StartConversationUseCase {

    private static final int MAX_ACTIVE_CONVERSATIONS = 3;

    private final ConversationRepository repository;
    private final AiTutorPort aiTutorPort;
    private final ApplicationEventPublisher eventPublisher;

    public StartConversationUseCase(ConversationRepository repository, AiTutorPort aiTutorPort,
                                     ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.aiTutorPort = aiTutorPort;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Conversation execute(UUID userId, String level, String topic)
            throws MaxConversationsExceededException, AiTutorException {

        int activeCount = repository.countActiveByUserId(userId);
        if (activeCount >= MAX_ACTIVE_CONVERSATIONS) {
            throw new MaxConversationsExceededException(MAX_ACTIVE_CONVERSATIONS);
        }

        ConversationLevel conversationLevel = new ConversationLevel(level);
        Conversation conversation = Conversation.start(userId, conversationLevel, topic);

        AiTutorPort.AiTutorResponse opening = aiTutorPort.chat(
                conversationLevel, topic, conversation.turns(), null);

        try {
            ConversationTurn assistantTurn = ConversationTurn.assistantTurn(opening.content(), opening.feedback());
            conversation = conversation.addTurn(assistantTurn);
        } catch (Exception e) {
            throw new AiTutorException("Failed to add opening turn", e);
        }

        Conversation saved = repository.save(conversation);
        conversation.pullDomainEvents().forEach(eventPublisher::publishEvent);
        return saved;
    }
}
