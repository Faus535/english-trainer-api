package com.faus535.englishtrainer.talk.application;

import com.faus535.englishtrainer.talk.domain.*;
import com.faus535.englishtrainer.talk.domain.error.TalkConversationAlreadyEndedException;
import com.faus535.englishtrainer.talk.infrastructure.InMemoryTalkConversationRepository;
import com.faus535.englishtrainer.talk.infrastructure.InMemoryTalkScenarioRepository;
import com.faus535.englishtrainer.talk.infrastructure.StubTalkAiPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SendTalkMessageUseCaseTest {

    private InMemoryTalkConversationRepository conversationRepository;
    private StubTalkAiPort aiPort;
    private SendTalkMessageUseCase useCase;

    @BeforeEach
    void setUp() {
        conversationRepository = new InMemoryTalkConversationRepository();
        InMemoryTalkScenarioRepository scenarioRepository = new InMemoryTalkScenarioRepository();
        aiPort = new StubTalkAiPort();
        useCase = new SendTalkMessageUseCase(conversationRepository, scenarioRepository, aiPort);
    }

    @Test
    void addsUserAndAssistantMessages() throws Exception {
        TalkConversation conversation = TalkConversationMother.active();
        conversationRepository.save(conversation);

        var result = useCase.execute(conversation.id().value(), "Hello there", null);

        assertNotNull(result.content());
        assertFalse(result.suggestEnd());

        TalkConversation updated = conversationRepository.findById(conversation.id()).orElseThrow();
        assertEquals(2, updated.messages().size());
        assertEquals("user", updated.messages().get(0).role());
        assertEquals("assistant", updated.messages().get(1).role());
    }

    @Test
    void includesCorrectionWhenPresent() throws Exception {
        TalkConversation conversation = TalkConversationMother.active();
        conversationRepository.save(conversation);
        aiPort.setCorrectionToReturn(new TalkCorrection(
                List.of("'I goed' → 'I went'"), List.of(), List.of(), "Nice try!", null));

        var result = useCase.execute(conversation.id().value(), "I goed to school", null);

        assertNotNull(result.correction());
        assertFalse(result.correction().grammarFixes().isEmpty());
    }

    @Test
    void suggestsEndOnFarewell() throws Exception {
        TalkConversation conversation = TalkConversationMother.active();
        conversationRepository.save(conversation);

        var result = useCase.execute(conversation.id().value(), "Thank you, goodbye!", null);

        assertTrue(result.suggestEnd());
    }

    @Test
    void throwsOnCompletedConversation() {
        TalkConversation conversation = TalkConversationMother.completed();
        conversationRepository.save(conversation);

        assertThrows(TalkConversationAlreadyEndedException.class,
                () -> useCase.execute(conversation.id().value(), "Hello", null));
    }

    @Test
    void execute_autoEndsAndGeneratesQuickSummary_whenQuickLimitReached() throws Exception {
        TalkConversation conversation = TalkConversationMother.quickModeWithUserMessages(2);
        conversationRepository.save(conversation);

        var result = useCase.execute(conversation.id().value(), "Thank you!", null);

        assertTrue(result.autoEnded());
        assertTrue(result.suggestEnd());

        TalkConversation updated = conversationRepository.findById(conversation.id()).orElseThrow();
        assertEquals(TalkStatus.COMPLETED, updated.status());
        assertNotNull(updated.summary());
    }
}
