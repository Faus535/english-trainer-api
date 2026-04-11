package com.faus535.englishtrainer.talk.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.faus535.englishtrainer.talk.domain.*;
import com.faus535.englishtrainer.talk.domain.error.TalkAiException;
import com.faus535.englishtrainer.talk.domain.error.TalkConversationNotFoundException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@UseCase
public class GetTalkConversationSummaryUseCase {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final TalkConversationRepository repository;
    private final TalkAiPort talkAiPort;

    public GetTalkConversationSummaryUseCase(TalkConversationRepository repository, TalkAiPort talkAiPort) {
        this.repository = repository;
        this.talkAiPort = talkAiPort;
    }

    @Transactional
    public TalkConversationSummaryResult execute(UUID conversationIdValue)
            throws TalkConversationNotFoundException, TalkAiException {
        TalkConversationId conversationId = new TalkConversationId(conversationIdValue);
        TalkConversation conversation = repository.findById(conversationId)
                .orElseThrow(() -> new TalkConversationNotFoundException(conversationId));

        if (conversation.mode() == ConversationMode.QUICK) {
            return deserializeQuickSummary(conversation.summary());
        }

        if (!conversation.hasGrammarFeedback()) {
            List<TalkMessage> userMessages = conversation.messages().stream()
                    .filter(m -> "user".equals(m.role())).toList();
            TalkAiPort.GrammarFeedback feedback = talkAiPort.analyzeGrammarAndVocabulary(userMessages);
            conversation = conversation.withGrammarFeedback(feedback.grammarNotes(), feedback.newVocabulary());
            repository.save(conversation);
        }

        return new TalkConversationSummaryResult.FullSummaryResult(
                conversation.summary(),
                conversation.evaluation(),
                conversation.messages().size(),
                conversation.errorCount(),
                conversation.grammarNotes() != null ? conversation.grammarNotes() : List.of(),
                conversation.newVocabulary() != null ? conversation.newVocabulary() : List.of()
        );
    }

    private TalkConversationSummaryResult.QuickSummaryResult deserializeQuickSummary(String json) {
        if (json == null || json.isBlank()) {
            return new TalkConversationSummaryResult.QuickSummaryResult(false, List.of(), "");
        }
        try {
            TalkAiPort.QuickSummary qs = MAPPER.readValue(json, TalkAiPort.QuickSummary.class);
            return new TalkConversationSummaryResult.QuickSummaryResult(
                    qs.taskCompleted(), qs.top3Corrections(), qs.encouragementNote());
        } catch (Exception e) {
            return new TalkConversationSummaryResult.QuickSummaryResult(false, List.of(), "");
        }
    }
}
