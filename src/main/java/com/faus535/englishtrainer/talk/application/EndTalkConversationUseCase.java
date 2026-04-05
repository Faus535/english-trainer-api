package com.faus535.englishtrainer.talk.application;

import com.faus535.englishtrainer.talk.domain.*;
import com.faus535.englishtrainer.talk.domain.error.TalkAiException;
import com.faus535.englishtrainer.talk.domain.error.TalkConversationAlreadyEndedException;
import com.faus535.englishtrainer.talk.domain.error.TalkConversationNotFoundException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@UseCase
public class EndTalkConversationUseCase {

    private final TalkConversationRepository repository;
    private final TalkAiPort talkAiPort;
    private final ApplicationEventPublisher eventPublisher;

    public EndTalkConversationUseCase(TalkConversationRepository repository, TalkAiPort talkAiPort,
                                       ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.talkAiPort = talkAiPort;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public EndTalkResult execute(UUID conversationIdValue)
            throws TalkConversationNotFoundException, TalkConversationAlreadyEndedException, TalkAiException {

        TalkConversationId conversationId = new TalkConversationId(conversationIdValue);
        TalkConversation conversation = repository.findById(conversationId)
                .orElseThrow(() -> new TalkConversationNotFoundException(conversationId));

        String summary = talkAiPort.summarize(conversation.level(), conversation.messages());
        TalkEvaluation evaluation = talkAiPort.evaluate(conversation.level(), conversation.messages());

        conversation = conversation.end(summary, evaluation);

        TalkConversation saved = repository.save(conversation);
        conversation.pullDomainEvents().forEach(eventPublisher::publishEvent);

        return new EndTalkResult(saved.summary(), saved.evaluation(),
                saved.messages().size(), saved.errorCount());
    }

    public record EndTalkResult(String summary, TalkEvaluation evaluation, int turnCount, int errorCount) {}
}
