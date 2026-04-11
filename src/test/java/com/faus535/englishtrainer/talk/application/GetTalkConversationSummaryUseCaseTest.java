package com.faus535.englishtrainer.talk.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.faus535.englishtrainer.talk.domain.*;
import com.faus535.englishtrainer.talk.domain.error.TalkAiException;
import com.faus535.englishtrainer.talk.domain.error.TalkConversationNotFoundException;
import com.faus535.englishtrainer.talk.infrastructure.InMemoryTalkConversationRepository;
import com.faus535.englishtrainer.talk.infrastructure.StubTalkAiPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GetTalkConversationSummaryUseCaseTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private InMemoryTalkConversationRepository repository;
    private StubTalkAiPort stubAiPort;
    private GetTalkConversationSummaryUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTalkConversationRepository();
        stubAiPort = new StubTalkAiPort();
        useCase = new GetTalkConversationSummaryUseCase(repository, stubAiPort);
    }

    @Test
    void shouldReturnFullSummaryWithGrammarFeedbackWhenFeedbackAlreadyPersisted() throws Exception {
        TalkConversation conversation = TalkConversationMother.completedWithGrammarFeedback();
        repository.save(conversation);

        TalkConversationSummaryResult result = useCase.execute(conversation.id().value());

        assertInstanceOf(TalkConversationSummaryResult.FullSummaryResult.class, result);
        TalkConversationSummaryResult.FullSummaryResult full =
                (TalkConversationSummaryResult.FullSummaryResult) result;
        assertEquals("Good session.", full.summary());
        assertFalse(full.grammarNotes().isEmpty());
        assertFalse(full.newVocabulary().isEmpty());
    }

    @Test
    void shouldAnalyzeAndPersistGrammarFeedbackWhenNotYetComputed() throws Exception {
        TalkConversation conversation = TalkConversationMother.completed();
        assertFalse(conversation.hasGrammarFeedback());
        repository.save(conversation);

        TalkConversationSummaryResult result = useCase.execute(conversation.id().value());

        assertInstanceOf(TalkConversationSummaryResult.FullSummaryResult.class, result);
        TalkConversationSummaryResult.FullSummaryResult full =
                (TalkConversationSummaryResult.FullSummaryResult) result;
        assertFalse(full.grammarNotes().isEmpty());

        TalkConversation persisted = repository.findById(conversation.id()).orElseThrow();
        assertTrue(persisted.hasGrammarFeedback());
    }

    @Test
    void shouldNotCallAiWhenGrammarFeedbackAlreadyPersisted() throws Exception {
        CountingStubTalkAiPort countingStub = new CountingStubTalkAiPort();
        GetTalkConversationSummaryUseCase useCaseWithCounting =
                new GetTalkConversationSummaryUseCase(repository, countingStub);

        TalkConversation conversation = TalkConversationMother.completedWithGrammarFeedback();
        repository.save(conversation);

        useCaseWithCounting.execute(conversation.id().value());

        assertEquals(0, countingStub.analyzeCallCount());
    }

    @Test
    void shouldReturnQuickSummaryWhenModeIsQuick() throws Exception {
        TalkAiPort.QuickSummary qs = new TalkAiPort.QuickSummary(
                true, List.of("I goed → I went"), "Great effort!");
        String summaryJson = MAPPER.writeValueAsString(qs);

        TalkConversation conversation = TalkConversation.reconstitute(
                TalkConversationId.generate(), UUID.randomUUID(), UUID.randomUUID(),
                new TalkLevel("b1"), ConversationMode.QUICK, TalkStatus.COMPLETED,
                summaryJson, null, null, null,
                Instant.now().minusSeconds(300), Instant.now(), List.of());
        repository.save(conversation);

        TalkConversationSummaryResult result = useCase.execute(conversation.id().value());

        assertInstanceOf(TalkConversationSummaryResult.QuickSummaryResult.class, result);
        TalkConversationSummaryResult.QuickSummaryResult quick =
                (TalkConversationSummaryResult.QuickSummaryResult) result;
        assertTrue(quick.taskCompleted());
        assertEquals(1, quick.top3Corrections().size());
        assertEquals("Great effort!", quick.encouragementNote());
    }

    @Test
    void shouldThrowNotFoundWhenConversationDoesNotExist() {
        assertThrows(TalkConversationNotFoundException.class,
                () -> useCase.execute(UUID.randomUUID()));
    }

    @Test
    void shouldReturnEmptyGrammarNotesWhenUserMessagesAreEmpty() throws Exception {
        TalkConversation conversation = TalkConversationMother.completed();
        repository.save(conversation);

        TalkConversationSummaryResult result = useCase.execute(conversation.id().value());

        assertInstanceOf(TalkConversationSummaryResult.FullSummaryResult.class, result);
        TalkConversationSummaryResult.FullSummaryResult full =
                (TalkConversationSummaryResult.FullSummaryResult) result;
        assertNotNull(full.grammarNotes());
        assertNotNull(full.newVocabulary());
    }

    private static class CountingStubTalkAiPort extends StubTalkAiPort {
        private int analyzeCount = 0;

        @Override
        public GrammarFeedback analyzeGrammarAndVocabulary(List<TalkMessage> messages) throws TalkAiException {
            analyzeCount++;
            return super.analyzeGrammarAndVocabulary(messages);
        }

        int analyzeCallCount() { return analyzeCount; }
    }
}
