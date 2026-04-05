package com.faus535.englishtrainer.talk.application;

import com.faus535.englishtrainer.talk.domain.*;
import com.faus535.englishtrainer.talk.domain.error.TalkConversationNotFoundException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;

import java.util.UUID;

@UseCase
public class GetTalkConversationSummaryUseCase {

    private final TalkConversationRepository repository;

    public GetTalkConversationSummaryUseCase(TalkConversationRepository repository) {
        this.repository = repository;
    }

    public TalkConversationSummary execute(UUID conversationIdValue) throws TalkConversationNotFoundException {
        TalkConversationId conversationId = new TalkConversationId(conversationIdValue);
        TalkConversation conversation = repository.findById(conversationId)
                .orElseThrow(() -> new TalkConversationNotFoundException(conversationId));

        return new TalkConversationSummary(
                conversation.summary(),
                conversation.evaluation(),
                conversation.messages().size(),
                conversation.errorCount()
        );
    }

    public record TalkConversationSummary(String summary, TalkEvaluation evaluation,
                                           int turnCount, int errorCount) {}
}
