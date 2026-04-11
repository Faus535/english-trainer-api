package com.faus535.englishtrainer.talk.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.faus535.englishtrainer.talk.domain.*;
import com.faus535.englishtrainer.talk.domain.error.TalkAiException;
import com.faus535.englishtrainer.talk.domain.error.TalkConversationAlreadyEndedException;
import com.faus535.englishtrainer.talk.domain.error.TalkConversationNotFoundException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@UseCase
public class SendTalkMessageUseCase {

    private static final int MAX_CONTEXT_MESSAGES = 8;
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Pattern FAREWELL_PATTERN = Pattern.compile(
            "\\b(bye|goodbye|good bye|see you|thanks for|thank you|that'?s all|gotta go|have to go|i'?m done|end chat)\\b",
            Pattern.CASE_INSENSITIVE
    );

    private final TalkConversationRepository repository;
    private final TalkScenarioRepository scenarioRepository;
    private final TalkAiPort talkAiPort;

    public SendTalkMessageUseCase(TalkConversationRepository repository,
                                   TalkScenarioRepository scenarioRepository,
                                   TalkAiPort talkAiPort) {
        this.repository = repository;
        this.scenarioRepository = scenarioRepository;
        this.talkAiPort = talkAiPort;
    }

    @Transactional
    public SendTalkMessageResult execute(UUID conversationIdValue, String content, Float confidence)
            throws TalkConversationNotFoundException, TalkConversationAlreadyEndedException, TalkAiException {

        TalkConversationId conversationId = new TalkConversationId(conversationIdValue);
        TalkConversation conversation = repository.findById(conversationId)
                .orElseThrow(() -> new TalkConversationNotFoundException(conversationId));

        TalkMessage userMessage = TalkMessage.userMessage(content);
        conversation = conversation.addMessage(userMessage);

        List<TalkMessage> contextMessages = conversation.recentMessages(MAX_CONTEXT_MESSAGES);

        TalkScenario scenario = conversation.scenarioId() != null
                ? scenarioRepository.findById(new TalkScenarioId(conversation.scenarioId())).orElse(null)
                : null;

        TalkAiPort.TalkAiResponse response = talkAiPort.chat(
                conversation.level(), scenario, contextMessages, confidence);

        TalkMessage assistantMessage = TalkMessage.assistantMessage(response.content(), response.correction());
        conversation = conversation.addMessage(assistantMessage);

        if (conversation.isAtQuickLimit()) {
            conversation = autoEndQuick(conversation);
            repository.save(conversation);
            return new SendTalkMessageResult(response.content(), response.correction(), true, true);
        }

        repository.save(conversation);

        boolean suggestEnd = detectFarewell(content) || conversation.isAtMaxTurns();
        return new SendTalkMessageResult(response.content(), response.correction(), suggestEnd, false);
    }

    private TalkConversation autoEndQuick(TalkConversation conversation) throws TalkAiException, TalkConversationAlreadyEndedException {
        TalkAiPort.QuickSummary quickSummary = talkAiPort.quickSummarize(conversation.messages());
        String summaryJson = serializeQuickSummary(quickSummary);
        return conversation.end(summaryJson, null);
    }

    private String serializeQuickSummary(TalkAiPort.QuickSummary quickSummary) {
        try {
            return MAPPER.writeValueAsString(quickSummary);
        } catch (Exception e) {
            return "{}";
        }
    }

    private boolean detectFarewell(String content) {
        return FAREWELL_PATTERN.matcher(content).find();
    }

    public record SendTalkMessageResult(String content, TalkCorrection correction,
                                         boolean suggestEnd, boolean autoEnded) {}
}
