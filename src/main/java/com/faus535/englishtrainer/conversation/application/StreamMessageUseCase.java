package com.faus535.englishtrainer.conversation.application;

import com.faus535.englishtrainer.conversation.domain.*;
import com.faus535.englishtrainer.conversation.domain.error.AiTutorException;
import com.faus535.englishtrainer.conversation.domain.error.ConversationAlreadyEndedException;
import com.faus535.englishtrainer.conversation.domain.error.ConversationNotFoundException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

@UseCase
public class StreamMessageUseCase {

    private static final int MAX_CONTEXT_TURNS = 10;

    private final ConversationRepository repository;
    private final AiTutorStreamPort aiTutorStreamPort;

    public StreamMessageUseCase(ConversationRepository repository, AiTutorStreamPort aiTutorStreamPort) {
        this.repository = repository;
        this.aiTutorStreamPort = aiTutorStreamPort;
    }

    @Transactional
    public Flux<String> execute(UUID conversationIdValue, String transcript, Float confidence)
            throws ConversationNotFoundException, ConversationAlreadyEndedException, AiTutorException {

        ConversationId conversationId = new ConversationId(conversationIdValue);
        Conversation conversation = repository.findById(conversationId)
                .orElseThrow(() -> new ConversationNotFoundException(conversationId));

        ConversationTurn userTurn = ConversationTurn.userTurn(transcript, confidence);
        Conversation updated = conversation.addTurn(userTurn);
        repository.save(updated);

        List<ConversationTurn> contextTurns = updated.recentTurns(MAX_CONTEXT_TURNS);

        return aiTutorStreamPort.chatStream(
                updated.level(), updated.topic(), contextTurns, confidence);
    }
}
