package com.faus535.englishtrainer.conversation.application;

import com.faus535.englishtrainer.conversation.domain.*;
import com.faus535.englishtrainer.conversation.domain.error.AiTutorException;
import com.faus535.englishtrainer.conversation.domain.error.ConversationAlreadyEndedException;
import com.faus535.englishtrainer.conversation.domain.error.ConversationNotFoundException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

@UseCase
public class StreamMessageUseCase {

    private static final Logger log = LoggerFactory.getLogger(StreamMessageUseCase.class);
    private static final int MAX_CONTEXT_TURNS = 8;

    private final ConversationRepository repository;
    private final AiTutorStreamPort aiTutorStreamPort;
    private final ApplicationEventPublisher eventPublisher;

    public StreamMessageUseCase(ConversationRepository repository, AiTutorStreamPort aiTutorStreamPort,
                                 ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.aiTutorStreamPort = aiTutorStreamPort;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Flux<AiTutorStreamPort.StreamEvent> execute(UUID conversationIdValue, String transcript, Float confidence)
            throws ConversationNotFoundException, ConversationAlreadyEndedException, AiTutorException {

        ConversationId conversationId = new ConversationId(conversationIdValue);
        Conversation conversation = repository.findById(conversationId)
                .orElseThrow(() -> new ConversationNotFoundException(conversationId));

        ConversationTurn userTurn = ConversationTurn.userTurn(transcript, confidence);
        Conversation updated = conversation.addTurn(userTurn);
        repository.save(updated);

        List<ConversationTurn> contextTurns = updated.recentTurns(MAX_CONTEXT_TURNS);

        return aiTutorStreamPort.chatStream(updated.level(), updated.topic(), contextTurns, confidence)
                .doOnNext(event -> {
                    if (event instanceof AiTutorStreamPort.StreamEvent.Feedback feedbackEvent) {
                        persistAssistantTurn(conversationIdValue, feedbackEvent);
                    }
                });
    }

    private void persistAssistantTurn(UUID conversationIdValue, AiTutorStreamPort.StreamEvent.Feedback feedbackEvent) {
        try {
            ConversationId conversationId = new ConversationId(conversationIdValue);
            Conversation conversation = repository.findById(conversationId).orElse(null);
            if (conversation == null) return;

            ConversationTurn assistantTurn = ConversationTurn.assistantTurn(
                    feedbackEvent.fullContent(), feedbackEvent.feedback());
            conversation = conversation.addTurn(assistantTurn);
            repository.save(conversation);

            TutorFeedback feedback = feedbackEvent.feedback();
            if (feedback != null && !feedback.vocabularySuggestions().isEmpty()) {
                eventPublisher.publishEvent(new VocabularyFeedbackEvent(
                        conversation.userId(), conversation.level().value(), feedback.vocabularySuggestions()));
            }
        } catch (Exception e) {
            log.error("Failed to persist stream assistant turn: {}", e.getMessage(), e);
        }
    }
}
