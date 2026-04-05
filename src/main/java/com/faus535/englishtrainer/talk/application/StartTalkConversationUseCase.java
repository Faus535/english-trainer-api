package com.faus535.englishtrainer.talk.application;

import com.faus535.englishtrainer.talk.domain.*;
import com.faus535.englishtrainer.talk.domain.error.TalkAiException;
import com.faus535.englishtrainer.talk.domain.error.TalkMaxConversationsExceededException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@UseCase
public class StartTalkConversationUseCase {

    private static final int MAX_ACTIVE_CONVERSATIONS = 1;

    private final TalkConversationRepository conversationRepository;
    private final TalkScenarioRepository scenarioRepository;
    private final TalkAiPort talkAiPort;
    private final ApplicationEventPublisher eventPublisher;

    public StartTalkConversationUseCase(TalkConversationRepository conversationRepository,
                                         TalkScenarioRepository scenarioRepository,
                                         TalkAiPort talkAiPort,
                                         ApplicationEventPublisher eventPublisher) {
        this.conversationRepository = conversationRepository;
        this.scenarioRepository = scenarioRepository;
        this.talkAiPort = talkAiPort;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public TalkConversation execute(UUID userId, UUID scenarioId, String level)
            throws TalkMaxConversationsExceededException, TalkAiException {

        int activeCount = conversationRepository.countActiveByUserId(userId);
        if (activeCount >= MAX_ACTIVE_CONVERSATIONS) {
            throw new TalkMaxConversationsExceededException(MAX_ACTIVE_CONVERSATIONS);
        }

        TalkLevel talkLevel = new TalkLevel(level);
        TalkScenario scenario = scenarioRepository.findById(new TalkScenarioId(scenarioId))
                .orElse(null);

        TalkConversation conversation = TalkConversation.start(userId, scenarioId, talkLevel);

        TalkAiPort.TalkAiResponse greeting = talkAiPort.chat(talkLevel, scenario,
                conversation.messages(), null);

        try {
            TalkMessage assistantMessage = TalkMessage.assistantMessage(
                    greeting.content(), greeting.correction());
            conversation = conversation.addMessage(assistantMessage);
        } catch (Exception e) {
            throw new TalkAiException("Failed to add greeting message", e);
        }

        return conversationRepository.save(conversation);
    }
}
