package com.faus535.englishtrainer.conversation.application;

import com.faus535.englishtrainer.conversation.domain.*;
import com.faus535.englishtrainer.conversation.domain.error.AiTutorException;
import com.faus535.englishtrainer.conversation.domain.error.ConversationAlreadyEndedException;
import com.faus535.englishtrainer.conversation.domain.error.ConversationNotFoundException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@UseCase
public class SendMessageUseCase {

    private static final int MAX_CONTEXT_TURNS = 10;

    private final ConversationRepository repository;
    private final AiTutorPort aiTutorPort;

    public SendMessageUseCase(ConversationRepository repository, AiTutorPort aiTutorPort) {
        this.repository = repository;
        this.aiTutorPort = aiTutorPort;
    }

    @Transactional
    public SendMessageResult execute(UUID conversationIdValue, String transcript, Float confidence)
            throws ConversationNotFoundException, ConversationAlreadyEndedException, AiTutorException {

        ConversationId conversationId = new ConversationId(conversationIdValue);
        Conversation conversation = repository.findById(conversationId)
                .orElseThrow(() -> new ConversationNotFoundException(conversationId));

        ConversationTurn userTurn = ConversationTurn.userTurn(transcript, confidence);
        conversation = conversation.addTurn(userTurn);

        List<ConversationTurn> contextTurns = conversation.recentTurns(MAX_CONTEXT_TURNS);

        AiTutorPort.AiTutorResponse response = aiTutorPort.chat(
                conversation.level(), conversation.topic(), contextTurns, confidence);

        ConversationTurn assistantTurn = ConversationTurn.assistantTurn(response.content(), response.feedback());
        conversation = conversation.addTurn(assistantTurn);

        repository.save(conversation);

        return new SendMessageResult(response.content(), response.feedback());
    }

    public record SendMessageResult(String content, TutorFeedback feedback) {}
}
