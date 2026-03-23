package com.faus535.englishtrainer.conversation.application;

import com.faus535.englishtrainer.conversation.domain.*;
import com.faus535.englishtrainer.conversation.domain.error.AiTutorException;
import com.faus535.englishtrainer.conversation.domain.error.ConversationAlreadyEndedException;
import com.faus535.englishtrainer.conversation.domain.error.ConversationNotFoundException;
import com.faus535.englishtrainer.errorpattern.infrastructure.event.GrammarFeedbackEvent;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

@UseCase
public class SendMessageUseCase {

    private static final int MAX_CONTEXT_TURNS = 8;
    private static final Pattern FAREWELL_PATTERN = Pattern.compile(
            "\\b(bye|goodbye|good bye|see you|thanks for|thank you|that'?s all|gotta go|have to go|i'?m done|end chat)\\b",
            Pattern.CASE_INSENSITIVE
    );

    private final ConversationRepository repository;
    private final AiTutorPort aiTutorPort;
    private final ApplicationEventPublisher eventPublisher;

    public SendMessageUseCase(ConversationRepository repository, AiTutorPort aiTutorPort,
                              ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.aiTutorPort = aiTutorPort;
        this.eventPublisher = eventPublisher;
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

        publishVocabularyEvent(conversation, response.feedback());

        boolean suggestEnd = detectFarewell(transcript);
        return new SendMessageResult(response.content(), response.feedback(), suggestEnd);
    }

    private void publishVocabularyEvent(Conversation conversation, TutorFeedback feedback) {
        if (feedback == null) return;
        if (!feedback.vocabularySuggestions().isEmpty()) {
            eventPublisher.publishEvent(new VocabularyFeedbackEvent(
                    conversation.userId(), conversation.level().value(), feedback.vocabularySuggestions()));
        }
        if (!feedback.grammarCorrections().isEmpty()) {
            eventPublisher.publishEvent(new GrammarFeedbackEvent(
                    conversation.userId(), feedback.grammarCorrections()));
        }
    }

    private boolean detectFarewell(String transcript) {
        return FAREWELL_PATTERN.matcher(transcript).find();
    }

    public record SendMessageResult(String content, TutorFeedback feedback, boolean suggestEnd) {}
}
