package com.faus535.englishtrainer.talk.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.faus535.englishtrainer.talk.domain.*;
import com.faus535.englishtrainer.talk.domain.error.TalkConversationNotFoundException;
import com.faus535.englishtrainer.talk.infrastructure.InMemoryTalkConversationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GetTalkConversationSummaryUseCaseTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private InMemoryTalkConversationRepository repository;
    private GetTalkConversationSummaryUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTalkConversationRepository();
        useCase = new GetTalkConversationSummaryUseCase(repository);
    }

    @Test
    void execute_returnsFullSummaryResult_whenModeIsFull() throws Exception {
        TalkConversation conversation = TalkConversationMother.completed();
        repository.save(conversation);

        TalkConversationSummaryResult result = useCase.execute(conversation.id().value());

        assertInstanceOf(TalkConversationSummaryResult.FullSummaryResult.class, result);
        TalkConversationSummaryResult.FullSummaryResult full =
                (TalkConversationSummaryResult.FullSummaryResult) result;
        assertEquals("Good session.", full.summary());
    }

    @Test
    void execute_returnsQuickSummaryResult_whenModeIsQuick() throws Exception {
        TalkAiPort.QuickSummary qs = new TalkAiPort.QuickSummary(
                true, List.of("I goed → I went"), "Great effort!");
        String summaryJson = MAPPER.writeValueAsString(qs);

        TalkConversation conversation = TalkConversation.reconstitute(
                TalkConversationId.generate(), UUID.randomUUID(), UUID.randomUUID(),
                new TalkLevel("b1"), ConversationMode.QUICK, TalkStatus.COMPLETED,
                summaryJson, null, Instant.now().minusSeconds(300), Instant.now(), List.of());
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
    void execute_throwsNotFound_whenConversationDoesNotExist() {
        assertThrows(TalkConversationNotFoundException.class,
                () -> useCase.execute(UUID.randomUUID()));
    }
}
